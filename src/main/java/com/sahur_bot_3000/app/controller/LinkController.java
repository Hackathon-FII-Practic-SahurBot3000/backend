package com.sahur_bot_3000.app.controller;

import com.sahur_bot_3000.app.dto.LinkRequest;
import com.sahur_bot_3000.app.model.Link;
import com.sahur_bot_3000.app.service.LinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/links")
@RequiredArgsConstructor
public class LinkController {
    private final LinkService linkService;

    @PostMapping
    public ResponseEntity<Link> createLink(@Valid @RequestBody LinkRequest request) {
        Link createdLink = linkService.createLink(request);
        return ResponseEntity.ok(createdLink);
    }
} 