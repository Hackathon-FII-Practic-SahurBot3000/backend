package com.sahur_bot_3000.app.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoteRequest {
    @NotNull(message = "Voted team ID is required")
    private Long votedTeamId;

    @NotNull(message = "Voter team ID is required")
    private Long voterTeamId;
} 