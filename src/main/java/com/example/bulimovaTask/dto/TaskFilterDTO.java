package com.example.bulimovaTask.dto;



public class TaskFilterDTO {
    private String status;
    private String title;

    private String username;

    private String deadlineFrom;
    private String deadlineTo;

    private String createdFrom;
    private String createdTo;

    private String updatedFrom;
    private String updatedTo;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDeadlineFrom() {
        return deadlineFrom;
    }

    public void setDeadlineFrom(String deadlineFrom) {
        this.deadlineFrom = deadlineFrom;
    }

    public String getDeadlineTo() {
        return deadlineTo;
    }

    public void setDeadlineTo(String deadlineTo) {
        this.deadlineTo = deadlineTo;
    }

    public String getCreatedFrom() {
        return createdFrom;
    }

    public void setCreatedFrom(String createdFrom) {
        this.createdFrom = createdFrom;
    }

    public String getCreatedTo() {
        return createdTo;
    }

    public void setCreatedTo(String createdTo) {
        this.createdTo = createdTo;
    }

    public String getUpdatedFrom() {
        return updatedFrom;
    }

    public void setUpdatedFrom(String updatedFrom) {
        this.updatedFrom = updatedFrom;
    }

    public String getUpdatedTo() {
        return updatedTo;
    }

    public void setUpdatedTo(String updatedTo) {
        this.updatedTo = updatedTo;
    }
}
