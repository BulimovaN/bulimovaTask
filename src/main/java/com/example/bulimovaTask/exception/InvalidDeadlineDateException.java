package com.example.bulimovaTask.exception;

public class InvalidDeadlineDateException extends RuntimeException {

    public InvalidDeadlineDateException() {
        super(ErrorMessage.DEADLINE_IN_PAST.getMessage());
    }
}
