package com.example.bulimovaTask.exception;

public enum ErrorMessage {

    INVALID_ROLE("Invalid role: "),

    USER_EMAIL_ALREADY_EXISTS("Email already exists: "),
    USER_ID_NOT_FOUND("User not found with id: "),
    USER_HAS_TASKS("User has tasks and cannot be deleted. User id: "),

    DEADLINE_REQUIRED("Deadline is required"),
    DEADLINE_IN_PAST("Deadline must be after current date"),

    DEADLINE_ALLOWED_ONLY_FOR_PENDING("Deadline can be set only when task status is PENDING"),
    UPDATE_ACCESS_DENIED("You can edit only your own tasks"),
    DELETE_ACCESS_DENIED("You can delete only your own tasks"),
    USER_TASKS_ACCESS_DENIED("You can view only your own tasks"),

    TASK_NOT_FOUND("Task not found with id = "),

    INVALID_TASK_STATUS("Invalid task status: "),
    INVALID_STATUS_TRANSITION("Invalid task status transition: "),
    INVALID_TASK_STATUS_FOR_DELETED("Invalid task status for delete: ");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
