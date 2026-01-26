package com.example.bulimovaTask.exception;

public class DeadlineRequiresPendingStatusException extends RuntimeException {

    public DeadlineRequiresPendingStatusException() {
        super(ErrorMessage.DEADLINE_ALLOWED_ONLY_FOR_PENDING.getMessage());
    }
}
