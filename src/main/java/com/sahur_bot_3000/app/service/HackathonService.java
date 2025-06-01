package com.sahur_bot_3000.app.service;

import com.sahur_bot_3000.app.dto.HackathonCreateRequest;
import com.sahur_bot_3000.app.dto.HackathonResponse;
import com.sahur_bot_3000.app.exception.ResourceNotFoundException;
import com.sahur_bot_3000.app.model.Enums.HackathonState;
import com.sahur_bot_3000.app.model.Enums.Role;
import com.sahur_bot_3000.app.model.Hackathon;
import com.sahur_bot_3000.app.model.HackathonTeam;
import com.sahur_bot_3000.app.model.User;
import com.sahur_bot_3000.app.repository.interfaces.HackathonRepository;
import com.sahur_bot_3000.app.repository.interfaces.HackathonTeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HackathonService {
    private final HackathonRepository hackathonRepository;
    private final HackathonTeamRepository hackathonTeamRepository;

    @Transactional
    public HackathonResponse createHackathon(HackathonCreateRequest request, User businessUser) {
        if (businessUser.getRole() != Role.BUSINESS) {
            throw new IllegalArgumentException("Only business accounts can create hackathons");
        }

        Hackathon hackathon = Hackathon.builder()
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .hackathonState(HackathonState.Pending)
                .startedAt(request.getStartedAt())
                .endedAt(request.getEndedAt())
                .prize(request.getPrize())
                .build();

        hackathon = hackathonRepository.save(hackathon);
        return mapToResponse(hackathon, false);
    }

    public HackathonResponse getHackathonById(Long id, Long userId) {
        Hackathon hackathon = hackathonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hackathon not found with id: " + id));
        
        boolean isParticipating = isUserParticipating(hackathon.getId(), userId);
        return mapToResponse(hackathon, isParticipating);
    }

    public List<HackathonResponse> getAllHackathons(Long userId) {
        return hackathonRepository.findAll().stream()
                .map(hackathon -> mapToResponse(hackathon, isUserParticipating(hackathon.getId(), userId)))
                .collect(Collectors.toList());
    }

    public List<HackathonResponse> getUserParticipatedHackathons(Long userId) {
        return hackathonRepository.findByUserId(userId).stream()
                .map(hackathon -> mapToResponse(hackathon, true))
                .collect(Collectors.toList());
    }

    public boolean isUserParticipatingInHackathon(Long hackathonId, Long userId) {
        return isUserParticipating(hackathonId, userId);
    }

    private boolean isUserParticipating(Long hackathonId, Long userId) {
        return hackathonTeamRepository.existsByHackathonIdAndUserId(hackathonId, userId);
    }

    private HackathonResponse mapToResponse(Hackathon hackathon, boolean isParticipating) {
        return HackathonResponse.builder()
                .id(hackathon.getId())
                .name(hackathon.getName())
                .description(hackathon.getDescription())
                .type(hackathon.getType())
                .hackathonState(hackathon.getHackathonState())
                .startedAt(hackathon.getStartedAt())
                .endedAt(hackathon.getEndedAt())
                .prize(hackathon.getPrize())
                .isParticipating(isParticipating)
                .build();
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
