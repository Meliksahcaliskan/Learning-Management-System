package com.lsm.model.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// For sending back token or user info upon successful login.
public class LoginRequestDTO {
    @NotBlank
    @Size(min = 3, max = 60)
    private String username;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}