package com.sahur_bot_3000.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamCreateRequest {
    @NotBlank(message = "Team name is required")
    private String name;

    @NotNull(message = "Hackathon ID is required")
    private Long hackathonId;

    @NotNull(message = "User ID is required")
    private Long userId;
} 