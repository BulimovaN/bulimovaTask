package com.example.bulimovaTask.exception;

public class InvalidRoleException extends RuntimeException {
    public InvalidRoleException(String role) {
        super(ErrorMessage.INVALID_ROLE.getMessage() + role);
    }
}