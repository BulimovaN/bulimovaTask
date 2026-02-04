package com.example.bulimovaTask.repository;

import com.example.bulimovaTask.entity.Task;
import com.example.bulimovaTask.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {


    List<Task> findByUser_Id(Long userId);

    boolean existsByUser_Id(Long userId);

    List<Task> findByStatusAndDeadlineBefore(
            TaskStatus status,
            LocalDateTime time
    );


}
