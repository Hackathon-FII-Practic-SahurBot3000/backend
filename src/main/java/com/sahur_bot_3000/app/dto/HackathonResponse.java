package com.sahur_bot_3000.app.dto;

import com.sahur_bot_3000.app.model.Enums.HackathonState;
import com.sahur_bot_3000.app.model.Enums.HackathonType;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class HackathonResponse {
    private Long id;
    private String name;
    private String description;
    private HackathonType type;
    private HackathonState hackathonState;
    private Date startedAt;
    private Date endedAt;
    private String prize;
    private boolean isParticipating;
}