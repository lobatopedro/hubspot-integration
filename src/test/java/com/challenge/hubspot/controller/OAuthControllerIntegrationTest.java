package com.challenge.hubspot.controller;

import com.challenge.hubspot.service.OAuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OAuthControllerIntegrationTest {

    private static final String AUTH_ENDPOINT = "/auth/authorize";
    private static final String MOCK_AUTH_URL = "https://app.hubspot.com/oauth/authorize?client_id=test-client&redirect_uri=http://localhost:8080/auth/callback";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OAuthService oAuthService;

    @Test
    @WithMockUser(username = "test-user", roles = {"USER"})
    void shouldGenerateAuthorizationUrl() throws Exception {
        doReturn(MOCK_AUTH_URL).when(oAuthService).getAuthorizationUrl();

        mockMvc.perform(get(AUTH_ENDPOINT))
                .andExpect(status().isOk())
                .andExpect(content().string(MOCK_AUTH_URL));
    }
}







