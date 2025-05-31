package com.sahur_bot_3000.app.service;

import com.sahur_bot_3000.app.dto.LinkRequest;
import com.sahur_bot_3000.app.model.Link;
import com.sahur_bot_3000.app.model.User;
import com.sahur_bot_3000.app.repository.interfaces.LinkRepository;
import com.sahur_bot_3000.app.repository.interfaces.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LinkService {
    private final LinkRepository linkRepository;
    private final UserRepository userRepository;

    @Transactional
    public Link createLink(LinkRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        Link link = Link.builder()
                .platformName(request.getPlatformName())
                .url(request.getUrl())
                .user(user)
                .build();

        return linkRepository.save(link);
    }
} 