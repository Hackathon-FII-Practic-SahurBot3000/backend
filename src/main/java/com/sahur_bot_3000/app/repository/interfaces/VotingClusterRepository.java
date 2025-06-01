package com.sahur_bot_3000.app.repository.interfaces;

import com.sahur_bot_3000.app.model.VotingCluster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VotingClusterRepository extends JpaRepository<VotingCluster, Long> {
    
    List<VotingCluster> findByHackathonIdAndIsActiveTrue(Long hackathonId);
    
    List<VotingCluster> findByHackathonIdAndClusterRound(Long hackathonId, Integer clusterRound);
    
    @Query("SELECT MAX(vc.clusterRound) FROM VotingCluster vc WHERE vc.hackathon.id = :hackathonId")
    Integer findMaxClusterRoundByHackathonId(@Param("hackathonId") Long hackathonId);
    
    boolean existsByHackathonIdAndClusterRound(Long hackathonId, Integer clusterRound);
} 