package com.sahur_bot_3000.app.repository.interfaces;

import com.sahur_bot_3000.app.model.HackathonTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HackathonTeamRepository extends JpaRepository<HackathonTeam, Long> {
    List<HackathonTeam> findByHackathonId(Long hackathonId);
} 