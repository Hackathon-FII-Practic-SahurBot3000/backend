package com.sahur_bot_3000.app.controller;

import com.sahur_bot_3000.app.dto.FairVotingAnalysisResponse;
import com.sahur_bot_3000.app.dto.FairVotingRequest;
import com.sahur_bot_3000.app.dto.VotingClusterResponse;
import com.sahur_bot_3000.app.service.FairVotingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fair-voting")
@RequiredArgsConstructor
public class FairVotingController {

    private final FairVotingService fairVotingService;

    @PostMapping("/generate-clusters")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<VotingClusterResponse>> generateFairVotingClusters(
            @RequestBody FairVotingRequest request) {
        List<VotingClusterResponse> clusters = fairVotingService.generateFairVotingClusters(request);
        return ResponseEntity.ok(clusters);
    }

    @GetMapping("/analyze/{hackathonId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<FairVotingAnalysisResponse> analyzeFairness(
            @PathVariable Long hackathonId) {
        FairVotingAnalysisResponse analysis = fairVotingService.analyzeFairness(hackathonId);
        return ResponseEntity.ok(analysis);
    }

    @GetMapping("/clusters/{hackathonId}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<VotingClusterResponse>> getVotingClusters(
            @PathVariable Long hackathonId) {
        List<VotingClusterResponse> clusters = fairVotingService.getVotingClusters(hackathonId);
        return ResponseEntity.ok(clusters);
    }
} 