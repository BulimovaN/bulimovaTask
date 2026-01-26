package com.example.bulimovaTask.exception;

import org.springframework.security.access.AccessDeniedException;

public class TaskUpdateAccessDeniedException extends AccessDeniedException {

    public TaskUpdateAccessDeniedException() {
        super(ErrorMessage.UPDATE_ACCESS_DENIED.getMessage());
    }
}
