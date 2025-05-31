package com.sahur_bot_3000.app.dto;

import com.sahur_bot_3000.app.model.Enums.HackathonType;
import lombok.Data;

@Data
public class HackathonRequest {
    private String name;
    private String description;
    private HackathonType type;
}

