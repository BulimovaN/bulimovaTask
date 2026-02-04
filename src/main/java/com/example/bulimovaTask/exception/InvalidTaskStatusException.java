package com.example.bulimovaTask.exception;

public class InvalidTaskStatusException extends RuntimeException {

    public InvalidTaskStatusException(String status) {
        super(ErrorMessage.INVALID_TASK_STATUS.getMessage() + status);
    }
}
