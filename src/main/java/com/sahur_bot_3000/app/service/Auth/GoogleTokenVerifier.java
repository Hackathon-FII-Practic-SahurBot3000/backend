package com.sahur_bot_3000.app.service.Auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@Service
public class GoogleTokenVerifier {

    public String verifyAndExtractEmail(String idToken) {
        try {
            URL url = new URL("https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            try (Scanner scanner = new Scanner(conn.getInputStream())) {
                String response = scanner.useDelimiter("\\A").next();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(response);
                return jsonNode.get("email").asText();
            }
        } catch (Exception e) {
            throw new RuntimeException("Token Google invalid", e);
        }
    }
}