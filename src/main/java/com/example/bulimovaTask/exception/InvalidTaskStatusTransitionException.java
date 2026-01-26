package com.example.bulimovaTask.exception;

import com.example.bulimovaTask.entity.TaskStatus;

public class InvalidTaskStatusTransitionException extends RuntimeException {
    private final TaskStatus currentStatus;
    private final TaskStatus targetStatus;

    public InvalidTaskStatusTransitionException(
            TaskStatus currentStatus,
            TaskStatus targetStatus
    ) {
        super(
                ErrorMessage.INVALID_STATUS_TRANSITION.getMessage()
                        + currentStatus + " -> " + targetStatus
        );
        this.currentStatus = currentStatus;
        this.targetStatus = targetStatus;
    }

    public TaskStatus getCurrentStatus() {
        return currentStatus;
    }

    public TaskStatus getTargetStatus() {
        return targetStatus;
    }
}
