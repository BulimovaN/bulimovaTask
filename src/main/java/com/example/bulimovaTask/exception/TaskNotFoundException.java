package com.example.bulimovaTask.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Long id) {
        super(ErrorMessage.TASK_NOT_FOUND.getMessage() + id);
    }
}
