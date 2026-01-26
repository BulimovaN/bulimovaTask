package com.example.bulimovaTask.dto;

import java.time.LocalDateTime;

public class MyTaskResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String status;
    private LocalDateTime deadline;
    private LocalDateTime  createdDate;
    private LocalDateTime  updateDate;



    public MyTaskResponseDTO(
            Long id,
            String title,
            String description,
            String status,
            LocalDateTime deadline,
            LocalDateTime createdDate,
                    LocalDateTime updateDate

    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.deadline = deadline;
        this.createdDate = createdDate;
        this.updateDate = updateDate;

    }

    public Long getId() {
        return id;
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

    public String getTitle() {
        return title;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }
}
