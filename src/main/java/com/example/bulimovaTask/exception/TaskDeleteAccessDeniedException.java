package com.example.bulimovaTask.exception;

public class TaskDeleteAccessDeniedException extends RuntimeException {


    public TaskDeleteAccessDeniedException() {
        super(ErrorMessage.DELETE_ACCESS_DENIED.getMessage());
    }
}
