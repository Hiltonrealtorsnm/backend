package com.realestate.realestate.service.impl;

import com.realestate.realestate.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // ✅ Your verified Brevo domain sender
    private static final String FROM_EMAIL = "office@hiltonrealtorsnm.com";
    private static final String FROM_NAME = "Hilton Realtors";

    @Override
    public void sendEmail(String to, String subject, String body) {

        String url = "https://api.brevo.com/v3/smtp/email";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("api-key", brevoApiKey);

        Map<String, Object> sender = Map.of(
                "name", FROM_NAME,
                "email", FROM_EMAIL
        );

        Map<String, Object> toMap = Map.of(
                "email", to
        );

        Map<String, Object> payload = new HashMap<>();
        payload.put("sender", sender);
        payload.put("to", new Object[]{toMap});
        payload.put("subject", subject);
        payload.put("htmlContent", body);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        ResponseEntity<String> response =
                restTemplate.postForEntity(url, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("❌ Brevo API email failed: " + response.getBody());
        }

        System.out.println("✅ Email sent successfully via Brevo API to: " + to);
    }
}
