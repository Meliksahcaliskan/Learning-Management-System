package com.lsm.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class ApiResponse {
    private boolean success;
    private String message;
    private Object data;
}

