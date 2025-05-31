package com.sahur_bot_3000.app.controller;

import com.sahur_bot_3000.app.dto.LinkRequest;
import com.sahur_bot_3000.app.dto.LinkResponse;
import com.sahur_bot_3000.app.dto.LinkUpdateRequest;
import com.sahur_bot_3000.app.service.LinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/links")
@RequiredArgsConstructor
public class LinkController {
    private final LinkService linkService;

    @PostMapping
    public ResponseEntity<Void> createLink(@Valid @RequestBody LinkRequest request) {
        linkService.createLink(request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateLink(
            @PathVariable Long id,
            @Valid @RequestBody LinkUpdateRequest request) {
        linkService.updateLink(id, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLink(@PathVariable Long id) {
        linkService.deleteLink(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LinkResponse>> getLinksByUserId(@PathVariable Long userId) {
        List<LinkResponse> links = linkService.getLinksByUserId(userId);
        return ResponseEntity.ok(links);
    }
} 