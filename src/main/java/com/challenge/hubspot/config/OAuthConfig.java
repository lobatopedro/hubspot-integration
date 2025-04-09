package com.challenge.hubspot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;

import java.util.Collections;

@Configuration
public class OAuthConfig {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(Collections.singletonList(hubSpotClientRegistration()));
    }

    private ClientRegistration hubSpotClientRegistration() {
        return ClientRegistration.withRegistrationId("hubspot")
                .clientId("${hubspot.client.id}")
                .clientSecret("${hubspot.client.secret}")
                .authorizationUri("https://app.hubspot.com/oauth/authorize")
                .tokenUri("https://api.hubapi.com/oauth/v1/token")
                .redirectUri("{baseUrl}/auth/callback")
                .scope("contacts")
                .authorizationGrantType(org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE)
                .build();
    }

}
