package com.challenge.hubspot.webhook;

import com.challenge.hubspot.webhook.processor.WebhookProcessor;
import com.challenge.hubspot.webhook.strategy.WebhookHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class WebhookProcessorTest {

    private WebhookProcessor webhookProcessor;

    @MockBean
    private WebhookHandler contactCreationWebhookHandler;

    @BeforeEach
    void setup() {
        when(contactCreationWebhookHandler.getEventType()).thenReturn("contact.creation");
        webhookProcessor = new WebhookProcessor(List.of(contactCreationWebhookHandler));
    }

    @Test
    void shouldProcessContactCreationWebhook() {
        Map<String, Object> eventData = Map.of("objectId", "12345");

        webhookProcessor.processEvent("contact.creation", eventData);

        verify(contactCreationWebhookHandler).handleEvent(eventData);
    }

}
