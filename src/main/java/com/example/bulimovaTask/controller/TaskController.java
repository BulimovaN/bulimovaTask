package com.example.bulimovaTask.controller;

import com.example.bulimovaTask.dto.*;



import com.example.bulimovaTask.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

@GetMapping
@Operation(summary = "Получить список задач с фильтрацией, пагинацией и сортировкой")
public ResponseEntity<PageResponse<TaskResponseDTO>> getAllTasks(
        @ModelAttribute TaskFilterDTO filter,
        @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "ASC") Sort.Direction direction
) {


    Pageable pageable = (page == null || size == null)
            ? Pageable.unpaged()
            : PageRequest.of(page, size, Sort.by(direction, sortBy));

    return ResponseEntity.ok(
            taskService.getAllTasks(filter, pageable)
    );
}

    @GetMapping("/{id}")
    public ResponseEntity<MyTaskResponseDTO> getTaskById(@PathVariable Long id) {
        MyTaskResponseDTO task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> register(
            @Valid @RequestBody TaskCreaterDTO dto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(dto));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<MyTaskResponseDTO>> getAllTasksForUser(
            @PathVariable Long userId
    ) {
        List<MyTaskResponseDTO> tasks = taskService.getAllTasksForUser(userId);

        if (tasks.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateDTO dto
    ) {
        return ResponseEntity.ok(taskService.updateTask(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {

        taskService.deleteTask(id);

        return ResponseEntity.noContent().build();
    }

}
