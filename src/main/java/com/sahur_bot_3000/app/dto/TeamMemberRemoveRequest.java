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
public class TeamMemberRemoveRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Team ID is required")
    private Long teamId;

    @NotNull(message = "Removed User ID is required")
    private Long removedUserId;
} 