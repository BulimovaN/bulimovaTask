package com.example.bulimovaTask.exception;

public class UserHasTasksException extends RuntimeException {
    public UserHasTasksException(Long userId) {
        super(ErrorMessage.USER_HAS_TASKS.getMessage()+ userId);
    }

}