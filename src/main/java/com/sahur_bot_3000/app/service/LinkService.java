package com.sahur_bot_3000.app.service;

import com.sahur_bot_3000.app.dto.LinkRequest;
import com.sahur_bot_3000.app.dto.LinkResponse;
import com.sahur_bot_3000.app.dto.LinkUpdateRequest;
import com.sahur_bot_3000.app.model.Link;
import com.sahur_bot_3000.app.model.User;
import com.sahur_bot_3000.app.repository.interfaces.LinkRepository;
import com.sahur_bot_3000.app.repository.interfaces.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional
    public void updateLink(Long linkId, LinkUpdateRequest request) {
        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new EntityNotFoundException("Link not found with id: " + linkId));

        link.setPlatformName(request.getPlatformName());
        link.setUrl(request.getUrl());

        linkRepository.save(link);
    }

    @Transactional
    public void deleteLink(Long linkId) {
        if (!linkRepository.existsById(linkId)) {
            throw new EntityNotFoundException("Link not found with id: " + linkId);
        }
        linkRepository.deleteById(linkId);
    }

    @Transactional(readOnly = true)
    public List<LinkResponse> getLinksByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }

        return linkRepository.findByUserId(userId).stream()
                .map(link -> LinkResponse.builder()
                        .id(link.getId())
                        .platformName(link.getPlatformName())
                        .url(link.getUrl())
                        .build())
                .toList();
    }
} 