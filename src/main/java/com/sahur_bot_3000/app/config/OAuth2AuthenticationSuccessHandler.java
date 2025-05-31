package com.sahur_bot_3000.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sahur_bot_3000.app.dto.AuthResponse;
import com.sahur_bot_3000.app.model.User;
import com.sahur_bot_3000.app.service.Auth.JwtService;
import com.sahur_bot_3000.app.service.Auth.OAuth2Service;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final OAuth2Service oAuth2Service;
    private final JwtService jwtService;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // Process the OAuth2 user and create/update user in database
        User user = oAuth2Service.processOAuth2User(oAuth2User);

        // Generate JWT token
        String jwt = jwtService.generateToken(user);

        System.out.println("frontendUrl: " + frontendUrl);
        String redirectUrl = frontendUrl + "/login" + "?token=" + jwt;

        response.sendRedirect(redirectUrl);
    }
}