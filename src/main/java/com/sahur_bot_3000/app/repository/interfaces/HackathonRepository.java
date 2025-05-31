package com.sahur_bot_3000.app.repository.interfaces;

import com.sahur_bot_3000.app.model.Hackathon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HackathonRepository extends JpaRepository<Hackathon, Long> {
} 