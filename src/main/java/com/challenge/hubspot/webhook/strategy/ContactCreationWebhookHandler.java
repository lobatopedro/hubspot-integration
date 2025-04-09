package com.challenge.hubspot.webhook.strategy;

import com.challenge.hubspot.service.ContactService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class ContactCreationWebhookHandler implements WebhookHandler {

    private final ContactService contactService;

    public ContactCreationWebhookHandler(ContactService contactService) {
        this.contactService = contactService;
    }

    @Override
    public String getEventType() {
        return "contact.creation";
    }

    @Override
    public void handleEvent(Map<String, Object> eventData) {
        log.info("Processing contact creation via webhook: {}", eventData);
        contactService.createContactFromWebhook(eventData);
    }
}


