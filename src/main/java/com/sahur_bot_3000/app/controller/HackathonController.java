package com.sahur_bot_3000.app.controller;

import com.sahur_bot_3000.app.dto.HackathonRequest;
import com.sahur_bot_3000.app.dto.HackathonResponse;
import com.sahur_bot_3000.app.service.HackathonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hackathons")
@RequiredArgsConstructor
public class HackathonController {

    private final HackathonService hackathonService;

    @PostMapping("/create")
    public ResponseEntity<HackathonResponse> createHackathon(
            @RequestBody HackathonRequest request,
            Authentication auth
    ) {
        return ResponseEntity.ok(hackathonService.createHackathon(request, auth.getName()));
    }

    @GetMapping
    public ResponseEntity<List<HackathonResponse>> getAll() {
        return ResponseEntity.ok(hackathonService.getAllHackathons());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HackathonResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(hackathonService.getHackathonById(id));
    }

    @GetMapping("/my")
    public ResponseEntity<List<HackathonResponse>> getMyHackathons(Authentication auth) {
        return ResponseEntity.ok(hackathonService.getMyHackathons(auth.getName()));
    }

}
