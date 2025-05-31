package com.sahur_bot_3000.app.service.Auth;

import com.sahur_bot_3000.app.model.User;
import com.sahur_bot_3000.app.model.Enums.Role;
import com.sahur_bot_3000.app.repository.interfaces.UserRepository;
import com.sahur_bot_3000.app.service.RoleService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2Service {

    private final UserRepository userRepository;
    private final RoleService roleService;

    public User processOAuth2User(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String firstName = oAuth2User.getAttribute("given_name");
        String lastName = oAuth2User.getAttribute("family_name");
        String profilePictureUrl = oAuth2User.getAttribute("picture");

        // Check if user already exists
        return userRepository.findByEmail(email)
                .map(existingUser -> updateExistingUser(existingUser, firstName, lastName, profilePictureUrl))
                .orElseGet(() -> createNewUser(email, firstName, lastName, profilePictureUrl));
    }

    private User updateExistingUser(User existingUser, String firstName, String lastName, String profilePictureUrl) {
        // Update user information from Google
        existingUser.setFirstName(firstName);
        existingUser.setLastName(lastName);
        existingUser.setProfilePictureUrl(profilePictureUrl);
        existingUser.setGoogleAccount(true);

        return userRepository.save(existingUser);
    }

    private User createNewUser(String email, String firstName, String lastName, String profilePictureUrl) {
        Role role = roleService.determineRoleFromEmail(email);

        User newUser = User.builder()
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .profilePictureUrl(profilePictureUrl)
                .googleAccount(true)
                .role(role)
                .password(null) // No password for OAuth2 users
                .build();

        return userRepository.save(newUser);
    }
}