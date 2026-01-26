package com.example.bulimovaTask.exception;

public class UserNotFoundException  extends RuntimeException {
    public UserNotFoundException(Long id) {
        super(ErrorMessage.USER_ID_NOT_FOUND.getMessage() + id);
    }
}
