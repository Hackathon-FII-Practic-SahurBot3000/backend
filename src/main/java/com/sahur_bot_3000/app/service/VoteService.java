package com.sahur_bot_3000.app.service;

import com.sahur_bot_3000.app.dto.VoteCountResponse;
import com.sahur_bot_3000.app.dto.VoteRequest;
import com.sahur_bot_3000.app.model.HackathonTeam;
import com.sahur_bot_3000.app.model.Vote;
import com.sahur_bot_3000.app.repository.interfaces.HackathonTeamRepository;
import com.sahur_bot_3000.app.repository.interfaces.VoteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final HackathonTeamRepository hackathonTeamRepository;

    @Transactional
    public void createVote(VoteRequest request) {
        // Check if both hackathon teams exist
        HackathonTeam votedTeam = hackathonTeamRepository.findById(request.getVotedTeamId())
                .orElseThrow(() -> new EntityNotFoundException("Voted team not found with id: " + request.getVotedTeamId()));
        
        HackathonTeam voterTeam = hackathonTeamRepository.findById(request.getVoterTeamId())
                .orElseThrow(() -> new EntityNotFoundException("Voter team not found with id: " + request.getVoterTeamId()));

        // Check if teams are different
        if (votedTeam.getId().equals(voterTeam.getId())) {
            throw new IllegalArgumentException("A team cannot vote for itself");
        }

        // Check if vote already exists
        if (voteRepository.existsByVotedTeamIdAndVoterTeamId(request.getVotedTeamId(), request.getVoterTeamId())) {
            throw new IllegalArgumentException("This team has already voted for the specified team");
        }

        Vote vote = Vote.builder()
                .votedTeam(votedTeam)
                .voterTeam(voterTeam)
                .build();

        voteRepository.save(vote);
    }

    @Transactional(readOnly = true)
    public VoteCountResponse getVoteCount(Long teamId) {
        // Check if team exists
        if (!hackathonTeamRepository.existsById(teamId)) {
            throw new EntityNotFoundException("Team not found with id: " + teamId);
        }

        long voteCount = voteRepository.countByVotedTeamId(teamId);
        
        return VoteCountResponse.builder()
                .teamId(teamId)
                .voteCount(voteCount)
                .build();
    }
} 