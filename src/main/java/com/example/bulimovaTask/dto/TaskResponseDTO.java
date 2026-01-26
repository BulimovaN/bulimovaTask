package com.example.bulimovaTask.dto;



import java.time.LocalDateTime;

public class TaskResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String status;
    private LocalDateTime deadline;
    private LocalDateTime  createdDate;
    private LocalDateTime  updatedDate;
    private TaskUserDTO user;



    public TaskResponseDTO(
            Long id,
            String title,
            String description,
            String status,
            LocalDateTime deadline,
            LocalDateTime createdDate,
            LocalDateTime updatedDate,
            TaskUserDTO user

    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.deadline = deadline;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.user = user;

    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public TaskUserDTO getUser() {
        return user;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }
}
