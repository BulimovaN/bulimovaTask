package com.example.bulimovaTask.service;


import com.example.bulimovaTask.configuration.CustomMetricsService;
import com.example.bulimovaTask.dto.PageResponse;
import com.example.bulimovaTask.dto.UserResponseDTO;
import com.example.bulimovaTask.dto.UserUpdateDTO;
import com.example.bulimovaTask.entity.Role;
import com.example.bulimovaTask.entity.User;
import com.example.bulimovaTask.exception.InvalidRoleException;
import com.example.bulimovaTask.exception.UserHasTasksException;
import com.example.bulimovaTask.exception.UserNotFoundException;
import com.example.bulimovaTask.repository.TaskRepository;
import com.example.bulimovaTask.repository.UserRepository;
import com.example.bulimovaTask.specification.UserSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@Service
@Transactional
public class UserService {


    private final UserRepository userRepository;
    private final TaskRepository taskRepository; // ← ДОБАВИТЬ
    private final CustomMetricsService metrics;

    public UserService(
            UserRepository userRepository,
            TaskRepository taskRepository,
            CustomMetricsService metrics
            ) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.metrics = metrics;
    }


    @Transactional(readOnly = true)
    public PageResponse<UserResponseDTO> findUsers(
            Long id,
            String username,
            String email,
            String role,
            String createFrom,
            String createTo,
            String updateFrom,
            String updateTo,
            Pageable pageable
    ) {


        Role roleEnum = null;
        if (role != null) {
            try {
                roleEnum = Role.valueOf(role);
            } catch (IllegalArgumentException ex) {
                log.warn("User filter: invalid role '{}' → empty result", role);
                return new PageResponse<>(List.of(), 0);
            }
        }

        LocalDateTime createFromDt = null;
        LocalDateTime createToDt = null;
        LocalDateTime updateFromDt = null;
        LocalDateTime updateToDt = null;

        try {
            if (createFrom != null) {
                createFromDt = LocalDateTime.parse(createFrom);
            }
            if (createTo != null) {
                createToDt = LocalDateTime.parse(createTo);
            }
            if (updateFrom != null) {
                updateFromDt = LocalDateTime.parse(updateFrom);
            }
            if (updateTo != null) {
                updateToDt = LocalDateTime.parse(updateTo);
            }
        } catch (DateTimeParseException ex) {
            log.warn(
                    "User filter: invalid date format (createFrom={}, createTo={}, updateFrom={}, updateTo={}) → empty result",
                    createFrom, createTo, updateFrom, updateTo
            );
            return new PageResponse<>(List.of(), 0);
        }


        if (createFromDt != null && createToDt != null && createFromDt.isAfter(createToDt)) {
            log.warn("User filter: createFrom > createTo → empty result");
            return new PageResponse<>(List.of(), 0);
        }

        if (updateFromDt != null && updateToDt != null && updateFromDt.isAfter(updateToDt)) {
            log.warn("User filter: updateFrom > updateTo → empty result");
            return new PageResponse<>(List.of(), 0);
        }


        Specification<User> spec = Specification.where(UserSpecification.hasId(id))
                .and(UserSpecification.usernameLike(username))
                .and(UserSpecification.emailLike(email))
                .and(UserSpecification.hasRole(roleEnum))
                .and(UserSpecification.createdFrom(createFromDt))
                .and(UserSpecification.createdTo(createToDt))
                .and(UserSpecification.updatedFrom(updateFromDt))
                .and(UserSpecification.updatedTo(updateToDt));

        Page<User> page = userRepository.findAll(spec, pageable);

        log.info(
                "User search: id={}, username={}, email={}, role={}, result={}",
                id, username, email, role, page.getTotalElements()
        );

        List<UserResponseDTO> users = page.getContent()
                .stream()
                .map(u -> new UserResponseDTO(
                        u.getId(),
                        u.getUsername(),
                        u.getEmail(),
                        u.getRole().name(),
                        u.getCreateDate(),
                        u.getUpdateDate()
                ))
                .toList();

        return new PageResponse<>(users, page.getTotalElements());
    }



    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreateDate(),
                user.getUpdateDate()
        );
    }




    public UserResponseDTO updateUser(Long id, UserUpdateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setUsername(dto.getUsername());
        if (dto.getRole() != null) {
            try {
                user.setRole(Role.valueOf(dto.getRole()));
            } catch (IllegalArgumentException ex) {
                throw new InvalidRoleException(dto.getRole());
            }
        }

        User saved = userRepository.saveAndFlush(user);

        metrics.userUpdated();

        return new UserResponseDTO(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getRole().name(),
                saved.getCreateDate(),
                saved.getUpdateDate()
        );

    }


    public void deleteUser(Long id) {

        ensureUserExists(id);

        if (taskRepository.existsByUser_Id(id)) {
            throw new UserHasTasksException(id);
        }

        userRepository.deleteById(id);
        metrics.userDeleted();
    }

    private void ensureUserExists(Long id) {

        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
    }

}