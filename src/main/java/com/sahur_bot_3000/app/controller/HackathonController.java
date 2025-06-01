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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/hackathons")
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

    @PostMapping("/{hackathonId}/teams/{teamId}/submission")
    public ResponseEntity<String> uploadSubmission(
            @PathVariable Long hackathonId,
            @PathVariable Long teamId,
            @RequestParam("file") MultipartFile file) throws IOException {
        String fileUrl = hackathonService.uploadSubmission(hackathonId, teamId, file);
        return ResponseEntity.ok(fileUrl);
    }

}
