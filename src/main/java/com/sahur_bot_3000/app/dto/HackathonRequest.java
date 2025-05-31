package com.sahur_bot_3000.app.dto;

import com.sahur_bot_3000.app.model.Enums.HackathonType;
import lombok.Data;

import java.util.Date;

@Data
public class HackathonRequest {
    private String name;
    private String description;
    private HackathonType type;
    private String prizes;

    private Date pendingAt;
    private Date startedAt;
    private Date votingAt;
    private Date endedAt;
}

