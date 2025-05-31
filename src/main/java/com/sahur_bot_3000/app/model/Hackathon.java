package com.sahur_bot_3000.app.model;

import com.sahur_bot_3000.app.model.Enums.HackathonState;
import com.sahur_bot_3000.app.model.Enums.HackathonType;
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
    @Column(name = "type")
    private HackathonType type;


    public String name;
    public String description;
    public String prize;

    private Date pendingAt;
    private Date startedAt;
    private Date votingAt;
    private Date endedAt;

    @Enumerated(EnumType.STRING)
    private HackathonState hackathonState;

    @OneToMany(mappedBy = "hackathon")
    private List<HackathonTeam> hackathonTeams;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;


}
