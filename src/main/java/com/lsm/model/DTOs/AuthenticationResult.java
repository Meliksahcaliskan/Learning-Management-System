package com.lsm.model.DTOs;

import com.lsm.model.entity.base.AppUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthenticationResult {
    private final String accessToken;
    private final String refreshToken;
    private final AppUser user;
    private final Long expiresIn;
}
