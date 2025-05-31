package com.sahur_bot_3000.app.dto;

import com.sahur_bot_3000.app.model.Enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Role role;
}