package com.example.bulimovaTask.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TaskCreaterDTO {

    @NotBlank(message = "Title must not be blank")
    @Size(min = 2, max = 100, message = "Title must be between 2 and 30 characters")
    private String title;

    @Size(max = 1000, message = "Tittle must be between 2 and 30 characters")
    private String description;





    public TaskCreaterDTO() {
    }

    public TaskCreaterDTO(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
