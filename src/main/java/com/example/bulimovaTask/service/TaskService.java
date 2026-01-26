package com.example.bulimovaTask.service;

import com.example.bulimovaTask.configuration.CustomMetricsService;
import com.example.bulimovaTask.dto.*;
import com.example.bulimovaTask.entity.Task;
import com.example.bulimovaTask.entity.TaskStatus;
import com.example.bulimovaTask.entity.User;
import com.example.bulimovaTask.exception.*;
import com.example.bulimovaTask.repository.TaskRepository;
import com.example.bulimovaTask.repository.UserRepository;
import com.example.bulimovaTask.specification.TaskSpecification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CustomMetricsService metrics;

    public TaskService(TaskRepository taskRepository,
                       UserRepository userRepository,
                       CustomMetricsService metrics) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.metrics = metrics;
    }



    @Transactional(readOnly = true)
    public PageResponse<TaskResponseDTO> getAllTasks(
            TaskFilterDTO filter,
            Pageable pageable
    ) {

        Long userId = null;

        if (filter.getUsername() != null) {
            userId = userRepository.findByUsername(filter.getUsername())
                    .map(User::getId)
                    .orElse(null);
        }
        log.debug(
                "Task filter params: status={}, username={}, createdFrom={}, createdTo={}, updatedFrom={}, updatedTo={}",
                filter.getStatus(),
                filter.getUsername(),
                filter.getCreatedFrom(),
                filter.getCreatedTo(),
                filter.getUpdatedFrom(),
                filter.getUpdatedTo()
        );
        Page<Task> taskPage = taskRepository.findAll(
                TaskSpecification.build(filter, userId),
                pageable
        );
        log.info(
                "Task search: status={}, username={}, result={}",
                filter.getStatus(),
                filter.getUsername(),
                taskPage.getTotalElements()
        );
        return mapToPageResponse(taskPage);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Transactional(readOnly = true)
    public MyTaskResponseDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        checkTaskViewOwner(task);

        return new MyTaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus().name(),
                task.getDeadline() != null
                        ? task.getDeadline()
                        : null,
                task.getCreateDate(),
                task.getUpdateDate()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public TaskResponseDTO createTask(TaskCreaterDTO dto) {
        User currentUser = getCurrentUser();


        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(TaskStatus.NEW);
        task.setUser(currentUser);
        task.setDeadline(null);

        Task saved = taskRepository.save(task);

        metrics.taskCreated();

        return new TaskResponseDTO(
                saved.getId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getStatus().name(),
                saved.getDeadline(),
                saved.getCreateDate(),
                saved.getUpdateDate(),
                new TaskUserDTO(
                        saved.getUser().getId(),
                        saved.getUser().getUsername()
                )
        );

    }


    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Transactional(readOnly = true)
    public List<MyTaskResponseDTO> getAllTasksForUser(Long userId) {

        ensureUserExists(userId);
        checkUserAccess(userId);


        return taskRepository.findByUser_Id(userId)
                .stream()
                .map(task -> new MyTaskResponseDTO(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getStatus().name(),
                        task.getDeadline() != null
                                ? task.getDeadline()
                                : null,
                        task.getCreateDate(),
                        task.getUpdateDate()

                ))
                .toList();
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public TaskResponseDTO updateTask(Long id, TaskUpdateDTO dto) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        checkTaskOwner(task);

        TaskStatus currentStatus = task.getStatus();
        TaskStatus targetStatus = parseStatus(dto.getStatus());
        LocalDateTime now = LocalDateTime.now();

        switch (currentStatus) {

            case NEW -> {
                task.setTitle(dto.getTitle());
                task.setDescription(dto.getDescription());

                if (targetStatus == TaskStatus.DONE || targetStatus == TaskStatus.EXPIRED) {
                    throw new InvalidTaskStatusTransitionException(currentStatus, targetStatus);
                }

                if (targetStatus == TaskStatus.NEW) {
                    if (dto.getDeadline() != null) {
                        throw new DeadlineRequiresPendingStatusException();
                    }
                }

                if (targetStatus == TaskStatus.PENDING) {
                    validateDeadline(dto.getDeadline(), now);
                    task.setDeadline(dto.getDeadline());
                    task.setStatus(TaskStatus.PENDING);
                }
            }

            case PENDING -> {
                if (targetStatus != TaskStatus.DONE) {
                    throw new InvalidTaskStatusTransitionException(
                            currentStatus, targetStatus
                    );
                }

                task.setDescription(dto.getDescription());
                task.setTitle(dto.getTitle());
                task.setStatus(TaskStatus.DONE);
            }

            case EXPIRED -> {
                if (targetStatus != TaskStatus.PENDING) {
                    throw new InvalidTaskStatusTransitionException(
                            currentStatus, targetStatus
                    );
                }

                validateDeadline(dto.getDeadline(), now);

                task.setDescription(dto.getDescription());
                task.setTitle(dto.getTitle());
                task.setDeadline(dto.getDeadline());
                task.setStatus(TaskStatus.PENDING);
            }

            case DONE -> throw new InvalidTaskStatusTransitionException(
                    currentStatus, targetStatus
            );
        }

        Task saved = taskRepository.saveAndFlush(task);
        metrics.taskUpdated();

        return new TaskResponseDTO(
                saved.getId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getStatus().name(),
                saved.getDeadline(),
                saved.getCreateDate(),
                saved.getUpdateDate(),
                saved.getUser() != null
                        ? new TaskUserDTO(
                        saved.getUser().getId(),
                        saved.getUser().getUsername()
                )
                        : null
        );
    }


    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTask(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        checkTaskOwnerForDelete(task);

        if (task.getStatus() != TaskStatus.NEW) {
            throw new InvalidTaskStatusForDeleteException(task.getStatus().name());
        }

        taskRepository.delete(task);
        metrics.taskDeleted();
    }


    private TaskStatus parseStatus(String status) {
        try {
            return TaskStatus.valueOf(status);
        } catch (IllegalArgumentException ex) {
            throw new InvalidTaskStatusException(status);
        }
    }


    private void validateDeadline(LocalDateTime deadline, LocalDateTime now) {

        if (deadline == null) {
            throw new DeadlineRequiredException();
        }

        if (!deadline.isAfter(now)) {
            throw new InvalidDeadlineDateException();
        }
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        return (Long) authentication.getPrincipal();
    }

    private User getCurrentUser() {
        Long userId = getCurrentUserId();

        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private boolean isAdmin() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private void checkTaskOwner(Task task) {

        if (isAdmin()) {
            return;
        }

        if (!task.getUser().getId().equals(getCurrentUserId())) {
            throw new TaskUpdateAccessDeniedException();
        }
    }

    private void checkTaskViewOwner(Task task) {

        if (isAdmin()) {
            return;
        }

        if (!task.getUser().getId().equals(getCurrentUserId())) {
            throw new UserTasksAccessDeniedException();
        }
    }

    private void checkTaskOwnerForDelete(Task task) {

        if (isAdmin()) {
            return;
        }

        if (!task.getUser().getId().equals(getCurrentUserId())) {
            throw new TaskDeleteAccessDeniedException();
        }
    }

    private void checkUserAccess(Long requestedUserId) {

        if (isAdmin()) {
            return;
        }

        if (!requestedUserId.equals(getCurrentUserId())) {
            throw new UserTasksAccessDeniedException();
        }
    }

    private void ensureUserExists(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }

    private PageResponse<TaskResponseDTO> mapToPageResponse(Page<Task> page) {

        List<TaskResponseDTO> tasks = page
                .map(task -> new TaskResponseDTO(
                        task.getId(),
                        task.getTitle(),
                        task.getDescription(),
                        task.getStatus().name(),
                        task.getDeadline(),
                        task.getCreateDate(),
                        task.getUpdateDate(),
                        task.getUser() != null
                                ? new TaskUserDTO(
                                task.getUser().getId(),
                                task.getUser().getUsername()
                        )
                                : null
                ))
                .getContent();

        return new PageResponse<>(
                tasks,
                page.getTotalElements()
        );
    }
}


