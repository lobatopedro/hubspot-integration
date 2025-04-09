package com.challenge.hubspot.service;

import com.challenge.hubspot.HubSpotClient;
import com.challenge.hubspot.factory.HubSpotContactFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

import static java.util.Objects.isNull;

@Service
@Slf4j
public class ContactService {

    private final HubSpotClient hubSpotClient;
    private final OAuthService oAuthService;

    public ContactService(HubSpotClient hubSpotClient, OAuthService oAuthService) {
        this.hubSpotClient = hubSpotClient;
        this.oAuthService = oAuthService;
    }

    public Mono<ResponseEntity<String>> createContact(String email, String firstName, String lastName, String phone, String company) {
        String accessToken = oAuthService.getAccessToken();

        if (isNull(accessToken)) {
            return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access token is missing"));
        }

        Map<String, Object> contactData = HubSpotContactFactory.createFullContact(email, firstName, lastName, phone, company);
        return createWithRetry(contactData, accessToken, 3);
    }

    public void createContactFromWebhook(Map<String, Object> eventData) {
        try {
            String email = (String) eventData.get("email");
            String firstName = (String) eventData.get("firstName");
            String lastName = (String) eventData.get("lastName");
            String phone = (String) eventData.get("phone");
            String company = (String) eventData.get("company");

            if (email == null || firstName == null) {
                log.warn("Dados insuficientes para criar contato: {}", eventData);
                return;
            }

            log.info("Criando contato via webhook: {}", eventData);
            createContact(email, firstName, lastName, phone, company).subscribe();
        } catch (Exception e) {
            log.error("Erro ao processar evento de criação de contato: {}", e.getMessage());
        }
    }

    private Mono<ResponseEntity<String>> handleWebClientException(WebClientResponseException ex,
                                                                  Map<String, Object> contactData,
                                                                  String accessToken,
                                                                  int retries) {
        if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS && retries > 0) {
            log.warn("Rate limit atingido. Tentando novamente...");
            return Mono.delay(Duration.ofSeconds(2))
                    .flatMap(ignored -> createWithRetry(contactData, accessToken, retries - 1));
        }
        log.error("Erro ao criar contato: {}", ex.getResponseBodyAsString());
        return Mono.just(ResponseEntity.status(ex.getStatusCode()).body("Erro: " + ex.getResponseBodyAsString()));
    }

    private Mono<ResponseEntity<String>> createWithRetry(Map<String, Object> contactData, String accessToken, int retries) {
        return hubSpotClient.createContact(contactData, accessToken)
                .map(response -> ResponseEntity.ok("Contato criado com sucesso! ID: " + response.get("id").asText()))
                .onErrorResume(WebClientResponseException.class, ex -> handleWebClientException(ex, contactData, accessToken, retries));
    }

}



