package com.sahur_bot_3000.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Hackathon hackathon;

    public String urlSubmission;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    public List<TeamMember> teamMembers;

    public String teamName;

    @OneToMany(mappedBy = "voterTeam")
    private List<Vote> votesGiven;

    @OneToMany(mappedBy = "votedTeam")
    private List<Vote> votesReceived;
}

