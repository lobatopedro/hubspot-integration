//package service;
//
//import com.challenge.hubspot.dto.ContactDTO;
//import com.challenge.hubspot.service.ContactService;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.mockserver.client.MockServerClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockserver.model.HttpRequest.request;
//import static org.mockserver.model.HttpResponse.response;
//
//@SpringBootTest
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//class ContactServiceIntegrationTest {
//
//    @Autowired
//    private ContactService contactService;
//
//    private MockServerClient mockServerClient;
//
//    @BeforeAll
//    void setUpMockServer() {
//        mockServerClient = new MockServerClient("127.0.0.1", 1080);
//    }
//
//    @AfterAll
//    void tearDownMockServer() {
//        mockServerClient.close();
//    }
//
//    @Test
//    void shouldCreateNewContactInHubSpot() {
//        // Arrange: Configurar o MockServer para retornar sucesso
//        mockServerClient.when(
//                request()
//                        .withMethod("POST")
//                        .withPath("/crm/v3/objects/contacts")
//        ).respond(
//                response()
//                        .withStatusCode(201)
//                        .withBody("{\"id\": \"12345\", \"properties\": {\"email\":\"test@example.com\"}}")
//        );
//
//        // Act: Criar um contato no HubSpot
//        ContactDTO contactDTO = new ContactDTO();
//        contactDTO.setEmail("test@example.com");
//        contactDTO.setFirstName("John");
//        contactDTO.setLastName("Doe");
//
//        String contactId = contactService.createContact(contactDTO);
//
//        // Assert: Garantir que o ID retornado é o esperado
//        assertNotNull(contactId);
//        assertEquals("12345", contactId);
//    }
//}
