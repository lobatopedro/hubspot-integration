package com.challenge.hubspot.webhook.strategy;

import java.util.Map;

public interface WebhookHandler {
    String getEventType();
    void handleEvent(Map<String, Object> eventData);
}


