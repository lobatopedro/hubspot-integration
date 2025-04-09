package com.challenge.hubspot.controller;

import com.challenge.hubspot.dto.OAuthTokenResponse;
import com.challenge.hubspot.service.OAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@Slf4j
public class OAuthController {

    private final OAuthService oAuthService;

    public OAuthController(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @GetMapping("/authorize")
    public ResponseEntity<String> authorize() {
        return ResponseEntity.ok(oAuthService.getAuthorizationUrl());
    }

    @GetMapping("/callback")
    public Mono<ResponseEntity<OAuthTokenResponse>> callback(@RequestParam("code") String code) {
        return oAuthService.exchangeAuthCodeForAccessToken(code)
                .map(ResponseEntity::ok)
                .onErrorResume(error -> {
                    //log.error("Erro ao trocar código por token: {}", error.getMessage());
                    return Mono.just(ResponseEntity.badRequest().body(null));
                });
    }
}


