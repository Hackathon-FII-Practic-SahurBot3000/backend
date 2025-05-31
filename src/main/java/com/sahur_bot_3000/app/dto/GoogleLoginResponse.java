package com.sahur_bot_3000.app.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Google OAuth2 login redirect information")
public class GoogleLoginResponse {

    @Schema(description = "URL to redirect the user for Google authentication", example = "/oauth2/authorization/google")
    private String authUrl;

    @Schema(description = "Information message about the redirect", example = "Redirect to this URL to start Google OAuth2 authentication")
    private String message;

    public GoogleLoginResponse(String authUrl, String message) {
        this.authUrl = authUrl;
        this.message = message;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public String getMessage() {
        return message;
    }
}