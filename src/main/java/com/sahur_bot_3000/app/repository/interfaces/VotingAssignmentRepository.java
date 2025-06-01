package com.sahur_bot_3000.app.repository.interfaces;

import com.sahur_bot_3000.app.model.VotingAssignment;
import com.sahur_bot_3000.app.model.Enums.VotingRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VotingAssignmentRepository extends JpaRepository<VotingAssignment, Long> {
    
    List<VotingAssignment> findByVotingClusterIdAndVotingRole(Long votingClusterId, VotingRole votingRole);
    
    List<VotingAssignment> findByTeamIdAndVotingRole(Long teamId, VotingRole votingRole);
    
    @Query("SELECT va FROM VotingAssignment va WHERE va.votingCluster.hackathon.id = :hackathonId AND va.team.id = :teamId AND va.votingRole = :votingRole")
    List<VotingAssignment> findByHackathonIdAndTeamIdAndVotingRole(@Param("hackathonId") Long hackathonId, 
                                                                   @Param("teamId") Long teamId, 
                                                                   @Param("votingRole") VotingRole votingRole);
    
    @Query("SELECT COUNT(va) FROM VotingAssignment va WHERE va.team.id = :teamId AND va.votingRole = :votingRole")
    Long countByTeamIdAndVotingRole(@Param("teamId") Long teamId, @Param("votingRole") VotingRole votingRole);
    
    @Query("SELECT va.team.id, COUNT(va) FROM VotingAssignment va WHERE va.votingCluster.hackathon.id = :hackathonId AND va.votingRole = :votingRole GROUP BY va.team.id")
    List<Object[]> countAssignmentsByTeamAndRole(@Param("hackathonId") Long hackathonId, @Param("votingRole") VotingRole votingRole);
} 