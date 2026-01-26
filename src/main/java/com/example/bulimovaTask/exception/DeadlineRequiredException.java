package com.example.bulimovaTask.exception;

public class DeadlineRequiredException extends RuntimeException {

    public DeadlineRequiredException() {
        super(ErrorMessage.DEADLINE_REQUIRED.getMessage());
    }
}
