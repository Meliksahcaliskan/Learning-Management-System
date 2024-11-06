package com.lsm.model.DTOs;

import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.Role;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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
}
