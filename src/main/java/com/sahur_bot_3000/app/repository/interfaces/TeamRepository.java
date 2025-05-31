package com.sahur_bot_3000.app.repository.interfaces;

import com.sahur_bot_3000.app.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
} 