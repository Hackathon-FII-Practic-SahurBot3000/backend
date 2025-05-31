package com.sahur_bot_3000.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "votes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "voted_team_id", nullable = false)
    private HackathonTeam votedTeam;

    @ManyToOne
    @JoinColumn(name = "voter_team_id", nullable = false)
    private HackathonTeam voterTeam;

}

