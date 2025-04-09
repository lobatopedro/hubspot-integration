package com.challenge.hubspot;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@Slf4j
public class HubSpotClient {

    private final WebClient webClient;

    public HubSpotClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.hubapi.com").build();
    }

    public Mono<JsonNode> createContact(Map<String, Object> contactData, String accessToken) {
        return webClient.post()
                .uri("/crm/v3/objects/contacts")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(Map.of("properties", contactData))
                .retrieve()
                .bodyToMono(JsonNode.class);
    }

}

