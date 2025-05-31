package com.sahur_bot_3000.app.repository.interfaces;

import com.sahur_bot_3000.app.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    Optional<TeamMember> findByTeamIdAndUserId(Long teamId, Long userId);
    boolean existsByTeamIdAndUserId(Long teamId, Long userId);
    Optional<TeamMember> findByUserIdAndTeamHackathonId(Long userId, Long hackathonId);
    List<TeamMember> findAllByTeamId(Long teamId);
} 