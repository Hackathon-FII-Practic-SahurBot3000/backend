package com.sahur_bot_3000.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "voting_clusters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotingCluster {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hackathon_id", nullable = false)
    private Hackathon hackathon;

    @Column(name = "cluster_round", nullable = false)
    private Integer clusterRound;

    @Column(name = "cluster_name", nullable = false)
    private String clusterName;

    @OneToMany(mappedBy = "votingCluster", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<VotingAssignment> votingAssignments = new ArrayList<>();

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
} 