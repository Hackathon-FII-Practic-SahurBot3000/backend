package com.sahur_bot_3000.app.service;


import com.sahur_bot_3000.app.dto.HackathonRequest;
import com.sahur_bot_3000.app.dto.HackathonResponse;
import com.sahur_bot_3000.app.model.Enums.HackathonState;
import com.sahur_bot_3000.app.model.Enums.Role;
import com.sahur_bot_3000.app.model.Hackathon;
import com.sahur_bot_3000.app.model.User;
import com.sahur_bot_3000.app.repository.interfaces.HackathonRepository;
import com.sahur_bot_3000.app.repository.interfaces.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HackathonService {

    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;

public HackathonResponse createHackathon(HackathonRequest request, String email) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    if (user.getRole() != Role.BUSINESS) {
        throw new RuntimeException("Only BUSINESS users can create hackathons.");
    }

    Hackathon hackathon = Hackathon.builder()
            .name(request.getName())
            .description(request.getDescription())
            .type(request.getType())
            .prizes(request.getPrizes())
            .pendingAt(request.getPendingAt())
            .startedAt(request.getStartedAt())
            .votingAt(request.getVotingAt())
            .endedAt(request.getEndedAt())
            .hackathonState(HackathonState.Pending)
            .createdBy(user)
            .build();

    hackathonRepository.save(hackathon);
    return mapToResponse(hackathon);
}

    public List<HackathonResponse> getAllHackathons() {
        return hackathonRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public HackathonResponse getHackathonById(Long id) {
        Hackathon h = hackathonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hackathon not found"));
        return mapToResponse(h);
    }

    private HackathonResponse mapToResponse(Hackathon h) {
        return HackathonResponse.builder()
                .id(h.getId())
                .name(h.getName())
                .description(h.getDescription())
                .type(h.getType())
                .hackathonState(h.getHackathonState())
                .startedAt(h.getStartedAt())
                .endedAt(h.getEndedAt())
                .build();
    }

    public List<HackathonResponse> getMyHackathons(String email) {
    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

    if (user.getRole() == Role.BUSINESS) {
        return hackathonRepository.findAll().stream()
                .filter(h -> h.getCreatedBy() != null && h.getCreatedBy().getEmail().equals(email))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    return user.getTeamMembers().stream()
            .map(tm -> tm.getTeam().getHackathon())
            .distinct()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
}

public void updateHackathonStates() {
    Date now = new Date();
    List<Hackathon> hackathons = hackathonRepository.findAll();

    for (Hackathon hackathon : hackathons) {
        if (hackathon.getHackathonState() == HackathonState.Ended) continue;

        long diffMillis = now.getTime() - hackathon.getStartedAt().getTime();
        long hoursPassed = diffMillis / (1000 * 60 * 60);

        if (hoursPassed >= 96 && hackathon.getHackathonState() != HackathonState.Ended) {
            hackathon.setHackathonState(HackathonState.Ended);
            hackathonRepository.save(hackathon);
        } else if (hoursPassed >= 48 && hackathon.getHackathonState() == HackathonState.Pending) {
            hackathon.setHackathonState(HackathonState.Ongoing);
            hackathonRepository.save(hackathon);
        }
    }
}

}
