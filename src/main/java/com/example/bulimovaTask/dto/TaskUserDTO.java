package com.example.bulimovaTask.dto;



public class TaskUserDTO {
    private Long id;

    private String username;

    public TaskUserDTO(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
