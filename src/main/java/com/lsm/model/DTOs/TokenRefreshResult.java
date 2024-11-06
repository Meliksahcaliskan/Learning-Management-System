package com.lsm.model.DTOs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenRefreshResult {
    private final String accessToken;
    private final String refreshToken;
}

