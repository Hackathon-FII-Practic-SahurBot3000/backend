package com.sahur_bot_3000.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OAuth2UserInfo {
    private String email;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;
    private String provider;
} 