package com.challenge.hubspot.service;

import com.challenge.hubspot.dto.OAuthTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Service
@Slf4j
public class OAuthService {

    private final WebClient webClient;
    private final ClientRegistration clientRegistration;
    private final OAuthTokenService tokenService;

    public OAuthService(ClientRegistrationRepository clientRegistrationRepository, WebClient.Builder webClientBuilder, OAuthTokenService tokenService) {
        this.clientRegistration = clientRegistrationRepository.findByRegistrationId("hubspot");
        this.webClient = webClientBuilder.baseUrl(clientRegistration.getProviderDetails().getTokenUri()).build();
        this.tokenService = tokenService;
    }

    public String getAccessToken() {
        String token = tokenService.getAccessToken();
        Duration expirationDuration = tokenService.getTokenExpiration();
        Instant expiration = Instant.now().plus(expirationDuration);

        if (token == null || Instant.now().isAfter(expiration)) {
            return refreshAccessToken();
        }
        return token;
    }

    public String getAuthorizationUrl() {
        return clientRegistration.getProviderDetails().getAuthorizationUri() + "?" +
                "client_id=" + clientRegistration.getClientId() +
                "&redirect_uri=" + clientRegistration.getRedirectUri() +
                "&response_type=code" +
                "&scope=contacts";
    }

    private String refreshAccessToken() {
        String refreshToken = tokenService.getRefreshToken();
        if (refreshToken == null) {
            throw new IllegalStateException("Nenhum refresh token disponível. Reautenticação necessária.");
        }

        Mono<OAuthTokenResponse> responseMono = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("grant_type", "refresh_token")
                        .queryParam("client_id", clientRegistration.getClientId())
                        .queryParam("client_secret", clientRegistration.getClientSecret())
                        .queryParam("refresh_token", refreshToken)
                        .build())
                .retrieve()
                .bodyToMono(OAuthTokenResponse.class);

        OAuthTokenResponse response = responseMono.block();
        if (response != null) {
            tokenService.saveAccessToken(response.getAccessToken(), Duration.ofSeconds(response.getExpiresIn()));
            tokenService.saveRefreshToken(response.getRefreshToken());
            return response.getAccessToken();
        } else {
            throw new IllegalStateException("Não foi possível obter um novo access token");
        }
    }

    public Mono<OAuthTokenResponse> exchangeAuthCodeForAccessToken(String code) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientRegistration.getClientId())
                        .queryParam("client_secret", clientRegistration.getClientSecret())
                        .queryParam("redirect_uri", clientRegistration.getRedirectUri())
                        .queryParam("code", code)
                        .build())
                .retrieve()
                .bodyToMono(OAuthTokenResponse.class)
                .doOnError(error -> log.error("Erro ao trocar código por token: {}", error.getMessage()));
    }

}

