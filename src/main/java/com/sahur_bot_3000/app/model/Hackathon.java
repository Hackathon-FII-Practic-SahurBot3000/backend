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
@Table(name = "hackathon")
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
    public HackathonType type;


    public String name;
    public String description;
    public String prize;

    public Date pendingAt;
    public Date startedAt;
    public Date votingAt;
    public Date endedAt;

    @Enumerated(EnumType.STRING)
    public HackathonState hackathonState;

    @OneToMany(mappedBy = "hackathon")
    private List<HackathonTeam> hackathonTeams;

    @ManyToOne
    @JoinColumn(name = "created_by")
    public User createdBy;

}
