package com.lsm.model.DTOs;

// For handling login requests (with fields like username and password).

import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.Role;

public class LoginResponseDTO {
    private Long id;
    private String username;
    private String email;
    private Role role;
    private String token;

    public LoginResponseDTO(AppUser appUser, String token) {
        this.id = appUser.getId();
        this.username = appUser.getUsername();
        this.email = appUser.getEmail();
        this.role = appUser.getRole();
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public String getToken() {
        return token;
    }
}
