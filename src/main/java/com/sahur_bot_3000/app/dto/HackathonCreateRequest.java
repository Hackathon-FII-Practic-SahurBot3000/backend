package com.sahur_bot_3000.app.dto;

import com.sahur_bot_3000.app.model.Enums.HackathonType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class HackathonCreateRequest {
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Type is required")
    private HackathonType type;
    
    @NotNull(message = "Start date is required")
    private Date startedAt;
    
    @NotNull(message = "End date is required")
    private Date endedAt;
    
    private String prize;
} 