package com.lms.lms.modules.AppUserManagement;

import org.springframework.security.crypto.bcrypt.BCrypt;

import com.lms.lms.modules.Role.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="app_users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 60)
    @Column(name="username", unique=true)
    private String username;

    @NotNull
    @Size(min = 5, max = 100)
    @Column(name="email", unique=true)
    private String email;

    @NotNull
    @Size(min = 6, max = 100)
    @Column(name="password")
    private String password;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name="role_id")
    private Role roleId;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String rawPassword) {
        this.password = BCrypt.hashpw(rawPassword, email);
    }

    public boolean checkPassword(String rawPassword) {
        return BCrypt.checkpw(rawPassword, this.password);
    }

    public Role getRoleId() {
        return roleId;
    }

    public void setRoleId(Role roleId) {
        this.roleId = roleId;
    }
}
