package com.sahur_bot_3000.app.repository.interfaces;

import com.sahur_bot_3000.app.model.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
} 