package com.sahur_bot_3000.app.controller;

import com.sahur_bot_3000.app.dto.HackathonCreateRequest;
import com.sahur_bot_3000.app.dto.HackathonResponse;
import com.sahur_bot_3000.app.model.User;
import com.sahur_bot_3000.app.service.HackathonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hackathon")
@RequiredArgsConstructor
public class HackathonController {
    private final HackathonService hackathonService;

    @PostMapping
    public ResponseEntity<HackathonResponse> createHackathon(
            @Valid @RequestBody HackathonCreateRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(hackathonService.createHackathon(request, user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HackathonResponse> getHackathonById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(hackathonService.getHackathonById(id, user.getId()));
    }

    @GetMapping
    public ResponseEntity<List<HackathonResponse>> getAllHackathons(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(hackathonService.getAllHackathons(user.getId()));
    }

    @GetMapping("/my-participations")
    public ResponseEntity<List<HackathonResponse>> getMyParticipatedHackathons(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(hackathonService.getUserParticipatedHackathons(user.getId()));
    }

    @GetMapping("/{id}/is-participating")
    public ResponseEntity<Boolean> isParticipatingInHackathon(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(hackathonService.isUserParticipatingInHackathon(id, user.getId()));
    }
}
