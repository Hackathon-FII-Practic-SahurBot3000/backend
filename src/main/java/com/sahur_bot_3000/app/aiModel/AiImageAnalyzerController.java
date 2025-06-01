package com.sahur_bot_3000.app.aiModel;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/api/ai")
public class AiImageAnalyzerController {

    private static final String PYTHON_SCRIPT = "E:/backend/src/main/java/com/sahur_bot_3000/app/aiModel/run_inference.py";

    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Double>> analyzeImage(@RequestParam("image") MultipartFile file) {
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
            Files.delete(tempFile);

            if (exitCode != 0) {
                throw new RuntimeException("Python script exited with code " + exitCode);
            }

            String jsonOutput = outputBuilder.toString();
            Map<String, Double> result = parseJsonOutput(jsonOutput);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", -1.0));
        }
    }

    private Map<String, Double> parseJsonOutput(String json) {
        Map<String, Double> result = new HashMap<>();
        json = json.replaceAll("[{}\"]", "");
        for (String pair : json.split(",")) {
            String[] kv = pair.split(":");
            result.put(kv[0].trim(), Double.parseDouble(kv[1].trim()));
        }
        return result;
    }
}
