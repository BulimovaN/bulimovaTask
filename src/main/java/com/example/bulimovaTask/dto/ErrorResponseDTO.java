package com.example.bulimovaTask.dto;

public class ErrorResponseDTO {
    private int status;
    private Object message;

    public ErrorResponseDTO(int status, Object message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public Object getMessage() {
        return message;
    }
}
