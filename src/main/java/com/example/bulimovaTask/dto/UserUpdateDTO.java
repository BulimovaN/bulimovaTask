package com.example.bulimovaTask.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserUpdateDTO {

    @NotBlank(message = "Username must not be blank")
    @Size(min = 2, max = 30, message = "Username must be between 2 and 30 characters")
    private String username;

    private String role;

    public UserUpdateDTO() {   // ← ВАЖНО
    }

    public UserUpdateDTO(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
