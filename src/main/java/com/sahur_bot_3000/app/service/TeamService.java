package com.sahur_bot_3000.app.service;

import com.sahur_bot_3000.app.dto.TeamCreateRequest;
import com.sahur_bot_3000.app.dto.TeamMemberInviteRequest;
import com.sahur_bot_3000.app.dto.TeamMemberRemoveRequest;
import com.sahur_bot_3000.app.dto.TeamMemberResponse;
import com.sahur_bot_3000.app.model.Hackathon;
import com.sahur_bot_3000.app.model.HackathonTeam;
import com.sahur_bot_3000.app.model.TeamMember;
import com.sahur_bot_3000.app.model.User;
import com.sahur_bot_3000.app.repository.interfaces.HackathonRepository;
import com.sahur_bot_3000.app.repository.interfaces.HackathonTeamRepository;
import com.sahur_bot_3000.app.repository.interfaces.TeamMemberRepository;
import com.sahur_bot_3000.app.repository.interfaces.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final HackathonTeamRepository hackathonTeamRepository;
    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public void createTeam(TeamCreateRequest request) {
        // Check if hackathon exists
        Hackathon hackathon = hackathonRepository.findById(request.getHackathonId())
                .orElseThrow(() -> new EntityNotFoundException("Hackathon not found with id: " + request.getHackathonId()));

        // Check if user exists
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getUserId()));

        // Create the hackathon team
        HackathonTeam hackathonTeam = HackathonTeam.builder()
                .teamName(request.getName())
                .hackathon(hackathon)
                .isJoined(true)
                .build();

        // Save the team first
        HackathonTeam savedTeam = hackathonTeamRepository.save(hackathonTeam);

        // Create and save the team member
        TeamMember teamMember = TeamMember.builder()
                .team(savedTeam)
                .user(user)
                .isOwner(true)
                .build();

        teamMemberRepository.save(teamMember);
    }

    @Transactional
    public void inviteTeamMember(TeamMemberInviteRequest request) {
        // Check if the inviter is a team owner
        TeamMember inviter = teamMemberRepository.findByTeamIdAndUserId(request.getTeamId(), request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Team member not found"));

        if (!inviter.isOwner()) {
            throw new IllegalStateException("Only team owners can invite new members");
        }

        // Check if the team exists
        HackathonTeam team = hackathonTeamRepository.findById(request.getTeamId())
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + request.getTeamId()));

        // Check if the invited user exists
        User invitedUser = userRepository.findById(request.getInvitedUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + request.getInvitedUserId()));

        // Check if user is already a team member
        if (teamMemberRepository.existsByTeamIdAndUserId(request.getTeamId(), request.getInvitedUserId())) {
            throw new IllegalStateException("User is already a member of this team");
        }

        // Create and save the new team member
        TeamMember newMember = TeamMember.builder()
                .team(team)
                .user(invitedUser)
                .isOwner(false)
                .build();

        teamMemberRepository.save(newMember);
    }

    @Transactional
    public void removeTeamMember(TeamMemberRemoveRequest request) {
        // Check if the remover is a team owner
        TeamMember remover = teamMemberRepository.findByTeamIdAndUserId(request.getTeamId(), request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Team member not found"));

        if (!remover.isOwner()) {
            throw new IllegalStateException("Only team owners can remove team members");
        }

        // Check if the team exists
        if (!hackathonTeamRepository.existsById(request.getTeamId())) {
            throw new EntityNotFoundException("Team not found with id: " + request.getTeamId());
        }

        // Check if the user to be removed is a team member
        TeamMember memberToRemove = teamMemberRepository.findByTeamIdAndUserId(request.getTeamId(), request.getRemovedUserId())
                .orElseThrow(() -> new EntityNotFoundException("Team member not found"));

        // Prevent removing the team owner
        if (memberToRemove.isOwner()) {
            throw new IllegalStateException("Cannot remove the team owner");
        }

        // Remove the team member
        teamMemberRepository.delete(memberToRemove);
    }

    @Transactional(readOnly = true)
    public List<TeamMemberResponse> getMyTeam(Long userId, Long hackathonId) {
        // Check if user exists
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found with id: " + userId);
        }

        // Check if hackathon exists
        if (!hackathonRepository.existsById(hackathonId)) {
            throw new EntityNotFoundException("Hackathon not found with id: " + hackathonId);
        }

        // Find the team member record for this user in this hackathon
        TeamMember userTeamMember = teamMemberRepository.findByUserIdAndTeamHackathonId(userId, hackathonId)
                .orElseThrow(() -> new EntityNotFoundException("User is not part of any team in this hackathon"));

        // Get all team members from the same team
        List<TeamMember> teamMembers = teamMemberRepository.findAllByTeamId(userTeamMember.getTeam().getId());

        // Convert to response DTOs
        return teamMembers.stream()
                .map(member -> TeamMemberResponse.builder()
                        .userId(member.getUser().getId())
                        .isOwner(member.isOwner())
                        .build())
                .collect(Collectors.toList());
    }
} 