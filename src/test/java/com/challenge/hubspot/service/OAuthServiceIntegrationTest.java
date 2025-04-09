package com.challenge.hubspot.service;

import com.challenge.hubspot.dto.OAuthTokenResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(locations = "classpath:application-test.yml") // 🔹 Garante que o arquivo será lido
class OAuthServiceIntegrationTest {


    @MockBean
    private OAuthService oAuthService;

    private static ClientAndServer mockServer;

    @BeforeAll
    static void startMockServer() {
        mockServer = ClientAndServer.startClientAndServer(1080);
    }

    @AfterAll
    static void stopMockServer() {
        if (mockServer != null) {
            mockServer.stop();
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("hubspot.token-uri", () -> "http://localhost:1080/oauth/v1/token");
    }

    @Test
    void shouldExchangeAuthorizationCodeForAccessToken() {
        new MockServerClient("localhost", 1080)
                .when(request()
                        .withMethod("POST")
                        .withPath("/oauth/v1/token"))
                .respond(response()
                        .withStatusCode(200)
                        .withBody("{\"access_token\":\"fakeAccessToken\",\"refresh_token\":\"fakeRefreshToken\",\"expires_in\":3600}"));

        // Act: Chama o serviço para trocar o código de autorização pelo token
        Mono<OAuthTokenResponse> responseMono = oAuthService.exchangeAuthCodeForAccessToken("authorizationCode");
        OAuthTokenResponse response = responseMono.block();

        // Assert
        assertNotNull(response);
        assertEquals("fakeAccessToken", response.getAccessToken());
        assertEquals("fakeRefreshToken", response.getRefreshToken());
        assertEquals(3600, response.getExpiresIn());
    }
}


