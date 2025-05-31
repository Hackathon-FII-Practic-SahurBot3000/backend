package com.sahur_bot_3000.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkUpdateRequest {
    @NotBlank(message = "Platform name is required")
    private String platformName;

    @NotBlank(message = "URL is required")
    private String url;
} 