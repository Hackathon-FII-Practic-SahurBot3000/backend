package com.sahur_bot_3000.app.service.Auth;

import com.sahur_bot_3000.app.dto.AuthRequest;
import com.sahur_bot_3000.app.dto.AuthResponse;
import com.sahur_bot_3000.app.dto.GoogleAuthRequest;
import com.sahur_bot_3000.app.model.Enums.Role;
import com.sahur_bot_3000.app.model.User;
import com.sahur_bot_3000.app.repository.interfaces.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final GoogleTokenVerifier googleTokenVerifier;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public AuthResponse register(AuthRequest request) {
        String email = request.getEmail();
        Role role = determineRoleFromEmail(email);

        var user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .googleAccount(false)
                .role(role)
                .build();

        userRepository.save(user);
        String jwt = jwtService.generateToken(user);
        return new AuthResponse(jwt, role);
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String jwt = jwtService.generateToken(user);
        return new AuthResponse(jwt, user.getRole());
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Role determineRoleFromEmail(String email) {
        if ( email.endsWith("@asii.com")) {
            return Role.BUSINESS;
        }
        return Role.USER;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
        return userRepository.findAll();
    }

    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Role getCurrentUserRole(String email) {
        return getCurrentUser(email).getRole();
    }

    public void promoteToBusiness(String email) {
        User user = getCurrentUser(email);
        user.setRole(Role.BUSINESS);
        userRepository.save(user);
    }

}