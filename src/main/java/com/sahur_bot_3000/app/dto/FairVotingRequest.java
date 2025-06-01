package com.sahur_bot_3000.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FairVotingRequest {
    private Long hackathonId;
    private Integer teamsPerCluster; // Number of teams that will vote in each cluster
    private Integer candidatesPerCluster; // Number of teams to be voted on in each cluster
    private Integer minVotingRounds; // Minimum number of rounds each team should participate as voter
} 