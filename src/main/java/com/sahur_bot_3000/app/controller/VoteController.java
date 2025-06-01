package com.sahur_bot_3000.app.controller;

import com.sahur_bot_3000.app.dto.VoteCountResponse;
import com.sahur_bot_3000.app.dto.VoteRequest;
import com.sahur_bot_3000.app.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/votes")
@RequiredArgsConstructor
public class VoteController {
    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<Void> createVote(@Valid @RequestBody VoteRequest request) {
        voteService.createVote(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count/{teamId}")
    public ResponseEntity<VoteCountResponse> getVoteCount(@PathVariable Long teamId) {
        VoteCountResponse response = voteService.getVoteCount(teamId);
        return ResponseEntity.ok(response);
    }
} 