package com.sahur_bot_3000.app.service;

import com.sahur_bot_3000.app.dto.TeamCreateRequest;
import com.sahur_bot_3000.app.model.Hackathon;
import com.sahur_bot_3000.app.model.Team;
import com.sahur_bot_3000.app.model.TeamParticipant;
import com.sahur_bot_3000.app.model.User;
import com.sahur_bot_3000.app.repository.interfaces.HackathonRepository;
import com.sahur_bot_3000.app.repository.interfaces.TeamRepository;
import com.sahur_bot_3000.app.repository.interfaces.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createTeam(TeamCreateRequest request) {
        // Check if hackathon exists
        Hackathon hackathon = hackathonRepository.findById(request.getHackathonId())
                .orElseThrow(() -> new EntityNotFoundException("Hackathon not found with id: " + request.getHackathonId()));

        // Check if user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        // Create the team
        Team team = Team.builder()
                .name(request.getName())
                .hackathon(hackathon)
                .build();

        Team savedTeam = teamRepository.save(team);

        // Create the team participant with isOwner = true
        TeamParticipant teamParticipant = TeamParticipant.builder()
                .team(savedTeam)
                .user(user)
                .isOwner(true)
                .build();

        // Add the participant to the team's list
        savedTeam.getParticipants().add(teamParticipant);
        teamRepository.save(savedTeam);
    }
} 