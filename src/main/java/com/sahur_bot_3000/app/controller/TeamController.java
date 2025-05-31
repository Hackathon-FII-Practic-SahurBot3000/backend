package com.sahur_bot_3000.app.controller;

import com.sahur_bot_3000.app.dto.GetMyTeamRequest;
import com.sahur_bot_3000.app.dto.TeamCreateRequest;
import com.sahur_bot_3000.app.dto.TeamMemberInviteRequest;
import com.sahur_bot_3000.app.dto.TeamMemberRemoveRequest;
import com.sahur_bot_3000.app.dto.TeamMemberResponse;
import com.sahur_bot_3000.app.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<Void> createTeam(@Valid @RequestBody TeamCreateRequest request) {
        teamService.createTeam(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/members")
    public ResponseEntity<Void> inviteTeamMember(@Valid @RequestBody TeamMemberInviteRequest request) {
        teamService.inviteTeamMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/members")
    public ResponseEntity<Void> removeTeamMember(@Valid @RequestBody TeamMemberRemoveRequest request) {
        teamService.removeTeamMember(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-team")
    public ResponseEntity<List<TeamMemberResponse>> getMyTeam(@Valid @RequestBody GetMyTeamRequest request) {
        List<TeamMemberResponse> teamMembers = teamService.getMyTeam(request.getUserId(), request.getHackathonId());
        return ResponseEntity.ok(teamMembers);
    }
} 