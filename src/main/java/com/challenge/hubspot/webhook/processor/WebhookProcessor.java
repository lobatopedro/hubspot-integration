package com.challenge.hubspot.webhook.processor;

import com.challenge.hubspot.webhook.strategy.WebhookHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class WebhookProcessor {

    private final Map<String, WebhookHandler> handlers;

    @Autowired
    public WebhookProcessor(List<WebhookHandler> webhookHandlers) {
        this.handlers = webhookHandlers.stream()
                .collect(Collectors.toMap(WebhookHandler::getEventType, handler -> handler));
    }

    public void processEvent(String eventType, Map<String, Object> eventData) {
        WebhookHandler handler = handlers.get(eventType);
        if (handler != null) {
            handler.handleEvent(eventData);
        } else {
            log.warn("Nenhum handler registrado para o evento: {}", eventType);
        }
    }
}



