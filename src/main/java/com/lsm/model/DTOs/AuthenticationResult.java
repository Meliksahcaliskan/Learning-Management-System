package com.lsm.model.DTOs;

import com.lsm.model.entity.base.AppUser;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthenticationResult {
    private final String accessToken;
    private final String refreshToken;
    private final AppUser user;
}
