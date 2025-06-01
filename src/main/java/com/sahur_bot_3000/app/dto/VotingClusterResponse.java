package com.sahur_bot_3000.app.dto;

import com.sahur_bot_3000.app.model.Enums.VotingRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VotingClusterResponse {
    private Long clusterId;
    private String clusterName;
    private Integer clusterRound;
    private List<TeamAssignment> teamAssignments;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TeamAssignment {
        private Long teamId;
        private String teamName;
        private VotingRole votingRole;
        private Integer assignmentOrder;
    }
} 