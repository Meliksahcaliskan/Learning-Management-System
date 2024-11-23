package com.lsm.model.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Login request containing user credentials")
public class LoginRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Size(min = 3, max = 60, message = "Username must be between 3 and 60 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain letters, numbers, dots, underscores, and hyphens")
    @Schema(description = "User's username (optional if email is provided)", example = "john.doe")
    private String username;

    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "Invalid email format")
    @Schema(description = "User's email (optional if username is provided)", example = "john.doe@example.com")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    @Schema(description = "User's password", example = "password123")
    private String password;

    @Schema(description = "Remember me flag for extended session", defaultValue = "false")
    private boolean rememberMe;

    @JsonIgnore
    public String getCredentials() {
        return this.password;
    }

    // Custom validation method
    // @JsonIgnore
    public boolean isValid() {
        return (username != null && !username.trim().isEmpty()) ||
                (email != null && !email.trim().isEmpty());
    }

    // @JsonIgnore
    public String getLoginIdentifier() {
        return username != null && !username.trim().isEmpty() ? username : email;
    }
}