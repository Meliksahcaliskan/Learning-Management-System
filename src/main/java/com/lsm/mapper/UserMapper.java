package com.lsm.mapper;

import com.lsm.model.DTOs.LoginResponseDTO;
import com.lsm.model.DTOs.RegisterResponseDTO;
import com.lsm.model.entity.base.AppUser;
import com.lsm.model.entity.enums.Role;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper {
    public LoginResponseDTO toLoginResponse(AppUser user, String accessToken, String refreshToken, Long expiresIn) {
        return LoginResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expiresIn)
                .issuedAt(Instant.now())
                .build();
    }

    public RegisterResponseDTO toRegisterResponse(AppUser user) {
        return RegisterResponseDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .message("Registration successful")
                .registeredAt(Instant.now())
                .nextSteps(getNextSteps(user))
                .build();
    }

    private List<String> getNextSteps(AppUser user) {
        List<String> steps = new ArrayList<>();
        steps.add("Please check your email for verification instructions");

        if (user.getRole() == Role.ROLE_STUDENT) {
            steps.add("Complete your student profile");
            steps.add("Join your class groups");
        } else if (user.getRole() == Role.ROLE_TEACHER) {
            steps.add("Complete your teacher profile");
            steps.add("Create your first class");
        }

        return steps;
    }
}
