package com.lsm.model.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TokenRefreshResult {
    private final String accessToken;
    private final String refreshToken;
}

