//package com.example.bulimovaTask.service;
//
//import com.example.bulimovaTask.entity.Role;
//import com.example.bulimovaTask.entity.Task;
//import com.example.bulimovaTask.entity.TaskStatus;
//import com.example.bulimovaTask.entity.User;
//import com.example.bulimovaTask.repository.TaskRepository;
//import com.example.bulimovaTask.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
////
//////@SpringBootTest
////@ActiveProfiles("test")
////@Transactional
////@ExtendWith(MockitoExtension.class)
////class TaskExpirationSchedulerTest {
////
////////    @Autowired
//////    private TaskRepository taskRepository;
//////
////////    @Autowired
//////    private TaskExpirationScheduler scheduler;
//////
////////    @Autowired
//////    private UserRepository userRepository;
////@Mock
////TaskRepository taskRepository;
////
//    @Mock
//    UserRepository userRepository;
//
//    @InjectMocks
//    TaskExpirationScheduler scheduler;
//    private User user;
//
//    @BeforeEach
//    void setUp() {
//        user = new User();
//        user.setUsername("user1");
//        user.setEmail("user1@mail.com");
//        user.setPassword("pass");
//        user.setRole(Role.ROLE_USER);
//
//        user = userRepository.saveAndFlush(user);
//    }
//
//    @Test
//    @DisplayName("Scheduler: PENDING task with expired deadline -> EXPIRED")
//    void shouldExpirePendingTask_whenDeadlinePassed() {
//        // given
//        Task task = new Task();
//        task.setTitle("Expired task");
//        task.setDescription("desc");
//        task.setStatus(TaskStatus.PENDING);
//        task.setDeadline(LocalDateTime.now().minusMinutes(5));
//        task.setUser(user);
//
//        task = taskRepository.saveAndFlush(task);
//
//        // when
//        scheduler.expirePendingTasks();
//
//        // then
//        Task updated = taskRepository.findById(task.getId()).orElseThrow();
//        assertThat(updated.getStatus()).isEqualTo(TaskStatus.EXPIRED);
//    }
//
//    @Test
//    @DisplayName("Scheduler: DONE task with expired deadline -> stays DONE")
//    void shouldNotExpireDoneTask_evenIfDeadlinePassed() {
//        // given
//        Task task = new Task();
//        task.setTitle("Done task");
//        task.setDescription("desc");
//        task.setStatus(TaskStatus.DONE);
//        task.setDeadline(LocalDateTime.now().minusMinutes(5));
//        task.setUser(user);
//
//        task = taskRepository.saveAndFlush(task);
//
//        // when
//        scheduler.expirePendingTasks();
//
//        // then
//        Task updated = taskRepository.findById(task.getId()).orElseThrow();
//        assertThat(updated.getStatus()).isEqualTo(TaskStatus.DONE);
//    }
//
//    @Test
//    @DisplayName("Scheduler: PENDING task with future deadline -> stays PENDING")
//    void shouldNotExpirePendingTask_whenDeadlineInFuture() {
//        // given
//        Task task = new Task();
//        task.setTitle("Future task");
//        task.setDescription("desc");
//        task.setStatus(TaskStatus.PENDING);
//        task.setDeadline(LocalDateTime.now().plusMinutes(10));
//        task.setUser(user);
//
//        task = taskRepository.saveAndFlush(task);
//
//        // when
//        scheduler.expirePendingTasks();
//
//        // then
//        Task updated = taskRepository.findById(task.getId()).orElseThrow();
//        assertThat(updated.getStatus()).isEqualTo(TaskStatus.PENDING);
//    }
//}

package com.example.bulimovaTask.service;



import com.example.bulimovaTask.entity.Task;
import com.example.bulimovaTask.entity.TaskStatus;
import com.example.bulimovaTask.repository.TaskRepository;
import com.example.bulimovaTask.service.TaskExpirationScheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskExpirationSchedulerTest {

    @Mock
    TaskRepository taskRepository;

    @InjectMocks
    TaskExpirationScheduler scheduler;

    private Task pendingExpired;
    private Task pendingFuture;
    private Task doneExpired;

    @BeforeEach
    void setUp() {
        pendingExpired = new Task();
        pendingExpired.setStatus(TaskStatus.PENDING);
        pendingExpired.setDeadline(LocalDateTime.now().minusMinutes(5));

        pendingFuture = new Task();
        pendingFuture.setStatus(TaskStatus.PENDING);
        pendingFuture.setDeadline(LocalDateTime.now().plusMinutes(10));

        doneExpired = new Task();
        doneExpired.setStatus(TaskStatus.DONE);
        doneExpired.setDeadline(LocalDateTime.now().minusMinutes(5));
    }

    @Test
    @DisplayName("PENDING + expired deadline → EXPIRED")
    void shouldExpirePendingTask_whenDeadlinePassed() {
        when(taskRepository.findByStatusAndDeadlineBefore(
                eq(TaskStatus.PENDING),
                any(LocalDateTime.class)
        )).thenReturn(List.of(pendingExpired));

        scheduler.expirePendingTasks();

        assertThat(pendingExpired.getStatus()).isEqualTo(TaskStatus.EXPIRED);
        verify(taskRepository).saveAllAndFlush(any());
    }

    @Test
    @DisplayName("DONE + expired deadline → stays DONE")
    void shouldNotExpireDoneTask_evenIfDeadlinePassed() {
        when(taskRepository.findByStatusAndDeadlineBefore(
                eq(TaskStatus.PENDING),
                any(LocalDateTime.class)
        )).thenReturn(List.of());

        scheduler.expirePendingTasks();

        assertThat(doneExpired.getStatus()).isEqualTo(TaskStatus.DONE);
        verify(taskRepository, never()).saveAllAndFlush(any());
    }

    @Test
    @DisplayName("PENDING + future deadline → stays PENDING")
    void shouldNotExpirePendingTask_whenDeadlineInFuture() {
        when(taskRepository.findByStatusAndDeadlineBefore(
                eq(TaskStatus.PENDING),
                any(LocalDateTime.class)
        )).thenReturn(List.of());

        scheduler.expirePendingTasks();

        assertThat(pendingFuture.getStatus()).isEqualTo(TaskStatus.PENDING);
        verify(taskRepository, never()).saveAllAndFlush(any());
    }
}
