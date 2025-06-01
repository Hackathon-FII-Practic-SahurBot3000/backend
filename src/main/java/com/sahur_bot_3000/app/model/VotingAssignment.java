package com.sahur_bot_3000.app.model;

import com.sahur_bot_3000.app.model.Enums.VotingRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "voting_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotingAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voting_cluster_id", nullable = false)
    private VotingCluster votingCluster;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private HackathonTeam team;

    @Enumerated(EnumType.STRING)
    @Column(name = "voting_role", nullable = false)
    private VotingRole votingRole; // VOTER or CANDIDATE

    @Column(name = "assignment_order")
    private Integer assignmentOrder; // For ensuring consistent ordering
} 