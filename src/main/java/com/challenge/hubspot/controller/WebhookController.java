package com.challenge.hubspot.controller;

import com.challenge.hubspot.service.WebhookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@Slf4j
public class WebhookController {

    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody String payload,
                                                @RequestHeader("X-HubSpot-Signature") String signature) {
        if (!webhookService.isValidSignature(payload, signature)) {
            log.warn("Invalid signature! Request rejected.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid signature");
        }

        log.info("Webhook received successfully: {}", payload);
        webhookService.processWebhook(payload);
        return ResponseEntity.ok("Webhook processed successfully!");
    }
}


