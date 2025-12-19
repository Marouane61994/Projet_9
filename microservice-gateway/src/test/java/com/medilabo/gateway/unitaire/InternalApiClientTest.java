package com.medilabo.gateway.unitaire;

import com.medilabo.gateway.service.InternalApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(InternalApiClient.class)
@TestPropertySource(properties = {
        "internal.base-url=http://internal-host:8080",
        "auth.patient.username=userP", "auth.patient.password=passP",
        "auth.note.username=userN", "auth.note.password=passN",
        "auth.diabetes.username=userD", "auth.diabetes.password=passD"
})
class InternalApiClientTest {

    @Autowired
    private InternalApiClient apiClient;

    @Autowired
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() throws Exception {
        Field field = InternalApiClient.class.getDeclaredField("restTemplate");
        field.setAccessible(true);
        RestTemplate restTemplate = (RestTemplate) field.get(apiClient);
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void testSendRequest_Success() {
        // Arrange
        String expectedUrl = "http://internal-host:8080/patient-service/patients/1";
        String mockResponse = "{\"id\":1, \"nom\":\"Dupont\"}";

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                // Vérifie que l'authentification Basic est bien générée
                .andExpect(header("Authorization", org.hamcrest.Matchers.startsWith("Basic ")))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

        // Act
        Object response = apiClient.sendRequest("/patient-service", "/patients/1", HttpMethod.GET, null, Map.class);

        // Assert
        assertNotNull(response);
        mockServer.verify();
    }

    @Test
    void testSendRequest_UnknownService_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            apiClient.sendRequest("/unknown-service", "/test", HttpMethod.GET, null, String.class);
        });
    }

    @Test
    void testSendRequest_PostMethod_WithBody() {
        // Arrange
        String expectedUrl = "http://internal-host:8080/note-service/notes";
        Map<String, String> body = Map.of("note", "Test note");

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess("{\"status\":\"saved\"}", MediaType.APPLICATION_JSON));

        // Act
        Object response = apiClient.sendRequest("/note-service", "/notes", HttpMethod.POST, body, Map.class);

        // Assert
        assertNotNull(response);
        mockServer.verify();
    }
}