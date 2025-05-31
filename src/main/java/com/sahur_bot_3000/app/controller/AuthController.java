package com.sahur_bot_3000.app.controller;

import com.sahur_bot_3000.app.dto.AuthRequest;
import com.sahur_bot_3000.app.dto.AuthResponse;
import com.sahur_bot_3000.app.dto.GoogleLoginResponse;
import com.sahur_bot_3000.app.service.Auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.sahur_bot_3000.app.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody AuthRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @GetMapping("/google")
    public ResponseEntity<GoogleLoginResponse> googleLogin() {
        String googleAuthUrl = "/oauth2/authorization/google";
        return ResponseEntity.ok(new GoogleLoginResponse(googleAuthUrl, "Redirect to this URL to start Google OAuth2 authentication"));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            User user = authService.getUserByEmail(email);
            return ResponseEntity.ok(Map.of(
                "email", user.getEmail(),
                "firstName", user.getFirstName() != null ? user.getFirstName() : "",
                "lastName", user.getLastName() != null ? user.getLastName() : "",
                "profilePictureUrl", user.getProfilePictureUrl() != null ? user.getProfilePictureUrl() : "",
                "userType", user.getUserType(),
                "googleAccount", user.isGoogleAccount()
            ));
        }
        return ResponseEntity.status(401).build();
    }
}
