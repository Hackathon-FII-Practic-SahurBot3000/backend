package com.sahur_bot_3000.app.aiModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PredictionResponse {
    private String result;
    private String heatmapBase64;
    private String imageId;
    private String explanation;
}
