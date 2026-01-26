package com.example.bulimovaTask.service;

import com.example.bulimovaTask.entity.Task;
import com.example.bulimovaTask.entity.TaskStatus;
import com.example.bulimovaTask.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Component
@Profile("!test")
public class TaskExpirationScheduler {


    private final TaskRepository taskRepository;

    public TaskExpirationScheduler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    @Scheduled(fixedRate = 60000)
    public void expirePendingTasks() {


        LocalDateTime now = LocalDateTime.now();

        List<Task> expiredTasks =
                taskRepository.findByStatusAndDeadlineBefore(
                        TaskStatus.PENDING,
                        now
                );
        log.info("Scheduler check at {}, found {} expired tasks",
                now, expiredTasks.size());
        if (expiredTasks.isEmpty()) {
            return;
        }

        expiredTasks.forEach(task ->
                task.setStatus(TaskStatus.EXPIRED)
        );


        taskRepository.saveAllAndFlush(expiredTasks);
    }
}
