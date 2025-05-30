package com.sahur_bot_3000.app.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}
