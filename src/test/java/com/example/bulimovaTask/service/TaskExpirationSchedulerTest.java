

package com.example.bulimovaTask.service;



import com.example.bulimovaTask.entity.Task;
import com.example.bulimovaTask.entity.TaskStatus;
import com.example.bulimovaTask.repository.TaskRepository;


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
