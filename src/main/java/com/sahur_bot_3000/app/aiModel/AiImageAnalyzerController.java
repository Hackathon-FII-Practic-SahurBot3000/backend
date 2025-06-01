package com.sahur_bot_3000.app.aiModel;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiImageAnalyzerController {

    private static final String PYTHON_SCRIPT = "E:/backend/src/main/java/com/sahur_bot_3000/app/aiModel/analyze_image.py";

    @PostMapping("/predict")
    public ResponseEntity<PredictionResponse> analyzeImage(@RequestParam("image") MultipartFile file) {
        try {
            Path tempFile = Files.createTempFile("upload_", ".jpg");
            Files.write(tempFile, file.getBytes());

            ProcessBuilder pb = new ProcessBuilder("python", PYTHON_SCRIPT, tempFile.toAbsolutePath().toString());
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder outputBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                outputBuilder.append(line);
            }

            int exitCode = process.waitFor();
            Files.deleteIfExists(tempFile);

            if (exitCode != 0) {
                throw new RuntimeException("Scriptul Python a eșuat cu codul: " + exitCode);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> resultMap = objectMapper.readValue(
                    outputBuilder.toString(),
                    new TypeReference<>() {}
            );

            PredictionResponse response = new PredictionResponse(
                    (String) resultMap.get("result"),
                    (String) resultMap.get("heatmap_base64"),
                    (String) resultMap.get("image_id"),
                    (String) resultMap.get("explanation")
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
