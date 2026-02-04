package com.example.bulimovaTask.exception;

public class InvalidTaskStatusForDeleteException extends RuntimeException {

    public InvalidTaskStatusForDeleteException(String status) {
        super(ErrorMessage.INVALID_TASK_STATUS_FOR_DELETED.getMessage() + status);
    }

}