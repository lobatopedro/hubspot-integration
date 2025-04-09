package com.challenge.hubspot;

import com.challenge.hubspot.webhook.processor.WebhookProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
class HubspotIntegrationApplicationTests {

	@MockitoBean
	private WebhookProcessor webhookProcessor;

	@Test
	void contextLoads() {
		// Apenas para testar se o contexto carrega corretamente
	}
}

