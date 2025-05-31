package com.sahur_bot_3000.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "hackathons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hackathon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Enumerated(EnumType.STRING)
    public HackathonType type;

    public String name;
    public String description;

    public Date startedAt;
    public Date endedAt;

    @OneToMany(mappedBy = "hackathon")
    public List<HackathonTeam> hackathonTeams;
}

enum HackathonType {
    Writing, Audio, Art, Business
}

