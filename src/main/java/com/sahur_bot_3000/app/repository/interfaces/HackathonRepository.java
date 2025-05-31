package com.sahur_bot_3000.app.repository.interfaces;

import com.sahur_bot_3000.app.model.Hackathon;
import com.sahur_bot_3000.app.model.Enums.HackathonState;
import com.sahur_bot_3000.app.model.Enums.HackathonType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HackathonRepository extends JpaRepository<Hackathon, Long> {
    List<Hackathon> findByHackathonState(HackathonState state);
}
