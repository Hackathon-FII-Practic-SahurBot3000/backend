package com.sahur_bot_3000.app.service.Auth;

import com.sahur_bot_3000.app.dto.AuthRequest;
import com.sahur_bot_3000.app.dto.AuthResponse;
import com.sahur_bot_3000.app.model.User;
import com.sahur_bot_3000.app.repository.interfaces.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(AuthRequest request) {
        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);
        String jwt = jwtService.generateToken(user);
        return new AuthResponse(jwt);
    }

    public AuthResponse login(AuthRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String jwt = jwtService.generateToken(user);
        return new AuthResponse(jwt);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}