package com.challenge.hubspot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static java.util.Objects.isNull;

@Service
@Slf4j
public class HubSpotService {

    private final WebClient webClient;
    private final OAuthService oAuthService;

    public HubSpotService(WebClient webClient, OAuthService oAuthService) {
        this.webClient = webClient;
        this.oAuthService = oAuthService;
    }

    public Mono<String> createContact(Map<String, Object> contactData) {
        String accessToken = oAuthService.getAccessToken();
        if (isNull(accessToken)) {
            return Mono.error(new IllegalStateException("Access Token not available"));
        }

        return webClient.post()
                .uri("https://api.hubapi.com/crm/v3/objects/contacts")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(contactData)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("Contact created successfully"))
                .doOnError(error -> log.error("Error creating contact: {}", error.getMessage()));
    }

}
