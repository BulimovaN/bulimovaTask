package com.example.bulimovaTask.exception;

public class UserTasksAccessDeniedException extends RuntimeException {

    public UserTasksAccessDeniedException() {
        super(ErrorMessage.USER_TASKS_ACCESS_DENIED.getMessage());
    }
}
