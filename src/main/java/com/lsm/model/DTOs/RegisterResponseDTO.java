package com.lsm.model.DTOs;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RegisterResponseDTO {
    private Long userId;
    private String message;
}
