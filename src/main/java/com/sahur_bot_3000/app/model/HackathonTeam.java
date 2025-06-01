package com.sahur_bot_3000.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hackathon_teams")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HackathonTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "hackathon_id")
    public Hackathon hackathon;

    public String urlSubmission;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = true)
    @Builder.Default
    public List<TeamMember> teamMembers = new ArrayList<>();

    public String teamName;

    @OneToMany(mappedBy = "voterTeam")
    @Builder.Default
    public List<Vote> votesGiven = new ArrayList<>();

    @OneToMany(mappedBy = "votedTeam")
    @Builder.Default
    public List<Vote> votesReceived = new ArrayList<>();

    @Column(name = "is_joined", nullable = false)
    public boolean isJoined;
}

