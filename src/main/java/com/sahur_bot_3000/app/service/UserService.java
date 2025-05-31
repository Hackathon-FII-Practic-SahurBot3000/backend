package com.sahur_bot_3000.app.service;

import org.springframework.stereotype.Service;

import com.sahur_bot_3000.app.model.User;
import com.sahur_bot_3000.app.repository.interfaces.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
