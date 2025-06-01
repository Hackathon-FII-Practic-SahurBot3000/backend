package com.sahur_bot_3000.app.repository.interfaces;

import com.sahur_bot_3000.app.model.Hackathon;
import com.sahur_bot_3000.app.model.Enums.HackathonState;
import com.sahur_bot_3000.app.model.Enums.HackathonType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HackathonRepository extends JpaRepository<Hackathon, Long> {
    List<Hackathon> findByHackathonState(HackathonState state);

    @Query("""
                SELECT DISTINCT h FROM Hackathon h
                JOIN h.hackathonTeams ht
                JOIN ht.teamMembers tm
                WHERE tm.user.id = :userId
            """)
    List<Hackathon> findByUserId(@Param("userId") Long userId);
}
