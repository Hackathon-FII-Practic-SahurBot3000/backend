package com.sahur_bot_3000.app.repository.interfaces;

import com.sahur_bot_3000.app.model.HackathonTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HackathonTeamRepository extends JpaRepository<HackathonTeam, Long> {

    @Query("SELECT CASE WHEN COUNT(ht) > 0 THEN true ELSE false END FROM HackathonTeam ht JOIN ht.teamMembers tm WHERE ht.hackathon.id = :hackathonId AND tm.user.id = :userId")
    boolean existsByHackathonIdAndUserId(Long hackathonId, Long userId);

}
}