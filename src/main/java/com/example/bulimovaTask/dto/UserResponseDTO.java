package com.example.bulimovaTask.dto;

import java.time.LocalDateTime;

public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    public UserResponseDTO(Long id, String username, String email, String role, LocalDateTime createDate, LocalDateTime updateDate) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }
}
