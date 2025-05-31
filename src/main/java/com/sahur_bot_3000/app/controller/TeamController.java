package com.sahur_bot_3000.app.controller;

import com.sahur_bot_3000.app.dto.TeamCreateRequest;
import com.sahur_bot_3000.app.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<Void> createTeam(@Valid @RequestBody TeamCreateRequest request) {
        teamService.createTeam(request);
        return ResponseEntity.noContent().build();
    }
} 