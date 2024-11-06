package com.lsm.model.DTOs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenRefreshResponseDTO {
    private String accessToken;
    private String refreshToken;
}
