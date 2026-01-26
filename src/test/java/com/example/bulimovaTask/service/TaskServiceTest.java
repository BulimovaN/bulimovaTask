package com.example.bulimovaTask.service;

import com.example.bulimovaTask.configuration.CustomMetricsService;
import com.example.bulimovaTask.dto.TaskCreaterDTO;
import com.example.bulimovaTask.dto.TaskUpdateDTO;
import com.example.bulimovaTask.entity.Role;
import com.example.bulimovaTask.entity.Task;
import com.example.bulimovaTask.entity.TaskStatus;
import com.example.bulimovaTask.entity.User;
import com.example.bulimovaTask.exception.*;
import com.example.bulimovaTask.repository.TaskRepository;
import com.example.bulimovaTask.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

//@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class TaskServiceTest {

//    @Mock
//    private TaskRepository taskRepository;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private CustomMetricsService metrics;
//
//    @InjectMocks
//    private TaskService taskService;

    @MockBean
    TaskRepository taskRepository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    CustomMetricsService customMetricsService;

    @Autowired
    TaskService taskService;

    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("user");
        user.setEmail("user@mail.com");
        user.setRole(Role.ROLE_USER);

        task = new Task();
        task.setId(10L);
        task.setTitle("Task");
        task.setDescription("Desc");
        task.setStatus(TaskStatus.NEW);
        task.setUser(user);

        SecurityContextHolder.clearContext();
    }

    // ================= CREATE =================

    @Test
    @DisplayName("CREATE: should create task with status NEW")
    void createTask_shouldCreateNewTask() {
        TaskCreaterDTO dto = new TaskCreaterDTO("Title", "Desc");

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        1L,
                        null,
                        List.of(() -> "ROLE_USER")
                )
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenAnswer(i -> i.getArgument(0));

        var response = taskService.createTask(dto);

        assertThat(response.getStatus()).isEqualTo("NEW");
//        verify(metrics).taskCreated();
    }

    // ================= UPDATE =================

    @Test
    @DisplayName("UPDATE: NEW -> PENDING with valid deadline")
    void updateTask_shouldMoveNewToPending() {
        TaskUpdateDTO dto = new TaskUpdateDTO(
                "New title",
                "New desc",
                "PENDING",
                LocalDateTime.now().plusDays(1)
        );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        1L,
                        null,
                        List.of(() -> "ROLE_USER")
                )
        );

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(taskRepository.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));

        var response = taskService.updateTask(10L, dto);

        assertThat(response.getStatus()).isEqualTo("PENDING");
//        verify(metrics).taskUpdated();
    }

    @Test
    @DisplayName("UPDATE: NEW with deadline but status NEW -> 400")
    void updateTask_shouldFail_whenNewWithDeadline() {
        TaskUpdateDTO dto = new TaskUpdateDTO(
                "Title",
                "Desc",
                "NEW",
                LocalDateTime.now().plusDays(1)
        );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        1L,
                        null,
                        List.of(() -> "ROLE_USER")
                )
        );

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.updateTask(10L, dto))
                .isInstanceOf(DeadlineRequiresPendingStatusException.class);
    }

    @Test
    @DisplayName("UPDATE: PENDING -> DONE")
    void updateTask_shouldMovePendingToDone() {
        task.setStatus(TaskStatus.PENDING);

        TaskUpdateDTO dto = new TaskUpdateDTO(
                "Title",
                "Desc",
                "DONE",
                null
        );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        1L,
                        null,
                        List.of(() -> "ROLE_USER")
                )
        );

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(taskRepository.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));

        var response = taskService.updateTask(10L, dto);

        assertThat(response.getStatus()).isEqualTo("DONE");
    }

    // ================= DELETE =================

    @Test
    @DisplayName("DELETE: admin can delete NEW task")
    void deleteTask_shouldDelete_whenAdminAndNew() {
        task.setStatus(TaskStatus.NEW);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        99L,
                        null,
                        List.of(() -> "ROLE_ADMIN")
                )
        );

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        taskService.deleteTask(10L);

        verify(taskRepository).delete(task);
//        verify(metrics).taskDeleted();
    }

    @Test
    @DisplayName("DELETE: cannot delete task with status DONE")
    void deleteTask_shouldFail_whenStatusNotNew() {
        task.setStatus(TaskStatus.DONE);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        99L,
                        null,
                        List.of(() -> "ROLE_ADMIN")
                )
        );

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> taskService.deleteTask(10L))
                .isInstanceOf(InvalidTaskStatusForDeleteException.class);

        verify(taskRepository, never()).delete(any(Task.class));
    }



    @Test
    @DisplayName("DELETE: task not found")
    void deleteTask_shouldFail_whenTaskNotFound() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        1L,
                        null,
                        List.of(() -> "ROLE_ADMIN")
                )
        );

        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTask(99L))
                .isInstanceOf(TaskNotFoundException.class);
    }
}
