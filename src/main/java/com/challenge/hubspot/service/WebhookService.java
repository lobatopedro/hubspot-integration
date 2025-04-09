package com.challenge.hubspot.service;

import com.challenge.hubspot.webhook.processor.WebhookProcessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
@Slf4j
public class WebhookService {

    private final String webhookSecret;
    private final WebhookProcessor webhookProcessor;
    private final ObjectMapper objectMapper;

    @Autowired
    public WebhookService(@Value("${hubspot.webhook.client-secret}") String webhookSecret,
                          WebhookProcessor webhookProcessor,
                          ObjectMapper objectMapper) {
        this.webhookSecret = webhookSecret;
        this.webhookProcessor = webhookProcessor;
        this.objectMapper = objectMapper;
    }

    public boolean isValidSignature(String payload, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] calculatedHash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(calculatedHash).equals(signature);
        } catch (Exception e) {
            log.error("Erro ao validar assinatura: {}", e.getMessage());
            return false;
        }
    }

    public void processWebhook(String payload) {
        try {
            JsonNode jsonNode = objectMapper.readTree(payload);
            for (JsonNode event : jsonNode) {
                String eventType = event.get("eventType").asText();
                Map<String, Object> eventData = objectMapper.convertValue(event, new TypeReference<>() {});

                log.info("Processando evento: {}", eventType);
                webhookProcessor.processEvent(eventType, eventData);
            }
        } catch (Exception e) {
            log.error("Erro ao processar webhook: {}", e.getMessage());
        }
    }
}


