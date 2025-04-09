package com.challenge.hubspot.service;

import com.challenge.hubspot.dto.OAuthTokenResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class OAuthServiceIntegrationTest {

    private static final String MOCK_SERVER_HOST = "localhost";
    private static final int MOCK_SERVER_PORT = 1080;
    private static final String TOKEN_ENDPOINT = "/oauth/v1/token";

    private static final String FAKE_CLIENT_ID = "fake-client-id";
    private static final String FAKE_CLIENT_SECRET = "fake-secret-id";
    private static final String FAKE_ACCESS_TOKEN = "fakeAccessToken";
    private static final String FAKE_REFRESH_TOKEN = "fakeRefreshToken";
    private static final int FAKE_EXPIRES_IN = 3600;

    @MockBean
    private OAuthService oAuthService;

    private static ClientAndServer mockServer;

    @BeforeAll
    static void startMockServer() {
        mockServer = ClientAndServer.startClientAndServer(MOCK_SERVER_PORT);
    }

    @AfterAll
    static void stopMockServer() {
        if (mockServer != null) {
            mockServer.stop();
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("hubspot.client-id", () -> FAKE_CLIENT_ID);
        registry.add("hubspot.client-secret", () -> FAKE_CLIENT_SECRET);
        registry.add("hubspot.token-uri", () -> "http://" + MOCK_SERVER_HOST + ":" + MOCK_SERVER_PORT + TOKEN_ENDPOINT);
    }

    @BeforeEach
    void setupMockServerClient() {
        new MockServerClient(MOCK_SERVER_HOST, MOCK_SERVER_PORT)
                .when(request()
                        .withMethod("POST")
                        .withPath(TOKEN_ENDPOINT))
                .respond(response()
                        .withStatusCode(200)
                        .withBody(String.format(
                                "{\"access_token\":\"%s\",\"refresh_token\":\"%s\",\"expires_in\":%d}",
                                FAKE_ACCESS_TOKEN, FAKE_REFRESH_TOKEN, FAKE_EXPIRES_IN
                        )));
    }

    @Test
    void shouldExchangeAuthorizationCodeForAccessToken() {
        OAuthTokenResponse fakeResponse = new OAuthTokenResponse(FAKE_ACCESS_TOKEN, FAKE_REFRESH_TOKEN, FAKE_EXPIRES_IN);
        when(oAuthService.exchangeAuthCodeForAccessToken("authorizationCode"))
                .thenReturn(Mono.just(fakeResponse));

        Mono<OAuthTokenResponse> responseMono = oAuthService.exchangeAuthCodeForAccessToken("authorizationCode");
        OAuthTokenResponse response = responseMono.block();

        assertNotNull(response);
        assertEquals(FAKE_ACCESS_TOKEN, response.getAccessToken());
        assertEquals(FAKE_REFRESH_TOKEN, response.getRefreshToken());
        assertEquals(FAKE_EXPIRES_IN, response.getExpiresIn());
    }
}



