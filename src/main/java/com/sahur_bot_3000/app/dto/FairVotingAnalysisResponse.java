package com.sahur_bot_3000.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FairVotingAnalysisResponse {
    private Long hackathonId;
    private Integer totalRounds;
    private Boolean isFair;
    private String fairnessMessage;
    private Map<Long, TeamFairnessStats> teamStats;
    private List<String> recommendations;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TeamFairnessStats {
        private Long teamId;
        private String teamName;
        private Integer timesAsVoter;
        private Integer timesAsCandidate;
        private Double fairnessScore; // Calculated based on deviation from ideal distribution
    }
} 