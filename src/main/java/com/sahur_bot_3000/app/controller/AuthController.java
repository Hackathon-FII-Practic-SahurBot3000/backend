package com.sahur_bot_3000.app.controller;

import com.sahur_bot_3000.app.dto.AuthRequest;
import com.sahur_bot_3000.app.dto.AuthResponse;
import com.sahur_bot_3000.app.dto.GoogleAuthRequest;
import com.sahur_bot_3000.app.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.sahur_bot_3000.app.model.User;
import org.springframework.http.ResponseEntity;
import java.util.List;


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

    @PostMapping("/google")
    public AuthResponse loginWithGoogle(@RequestBody GoogleAuthRequest request) {
        return authService.loginWithGoogle(request);
    }

    @GetMapping("/test-db")
    public ResponseEntity<String> testDb() {
        List<User> users = authService.getAllUsers();
        return ResponseEntity.ok("Total users: " + users.size());
    }

}
