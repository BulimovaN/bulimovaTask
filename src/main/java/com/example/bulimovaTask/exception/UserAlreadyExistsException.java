package com.example.bulimovaTask.exception;

public class UserAlreadyExistsException extends RuntimeException{
    public UserAlreadyExistsException(String email) {
        super(ErrorMessage.USER_EMAIL_ALREADY_EXISTS.getMessage() + email);
    }
}
