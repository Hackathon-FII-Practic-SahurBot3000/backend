package com.sahur_bot_3000.app.repository.interfaces;

import com.sahur_bot_3000.app.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByVotedTeamIdAndVoterTeamId(Long votedTeamId, Long voterTeamId);
    long countByVotedTeamId(Long votedTeamId);
} 