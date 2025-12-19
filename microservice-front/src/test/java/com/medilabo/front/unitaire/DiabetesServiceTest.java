package com.medilabo.front.unitaire;

import com.medilabo.front.service.DiabetesService;
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
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(DiabetesService.class)
@TestPropertySource(properties = {
        "gateway.url=http://localhost:8083",
        "auth.gateway.username=admin",
        "auth.gateway.password=password"
})
class DiabetesServiceTest {

    @Autowired
    private DiabetesService diabetesService;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() throws Exception {
        Field field = DiabetesService.class.getDeclaredField("restTemplate");
        field.setAccessible(true);
        RestTemplate restTemplate = (RestTemplate) field.get(diabetesService);
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void testGetDiabetesReport_Success() {
        // Arrange
        Long patientId = 1L;
        String expectedUrl = "http://localhost:8083/diabetes-service/assess/" + patientId;
        String mockJsonResponse = "{\"patientId\":1, \"niveauRisque\":\"Borderline\"}";

        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", org.hamcrest.Matchers.startsWith("Basic ")))
                .andRespond(withSuccess(mockJsonResponse, MediaType.APPLICATION_JSON));

        // Act
        Map<String, Object> result = diabetesService.getDiabetesReport(patientId);

        // Assert
        assertNotNull(result);
        assertEquals("Borderline", result.get("niveauRisque"));
        mockServer.verify();
    }

    @Test
    void testGetDiabetesReport_ErrorHandling() {
        // Arrange
        Long patientId = 1L;
        String expectedUrl = "http://localhost:8083/diabetes-service/assess/" + patientId;

        // On simule une erreur serveur (500)
        mockServer.expect(requestTo(expectedUrl))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        // Act
        Map<String, Object> result = diabetesService.getDiabetesReport(patientId);

        // Assert : On vérifie que le catch renvoie bien la map d'erreur définie dans votre service
        assertNotNull(result);
        assertTrue(result.containsKey("error"));
        assertEquals("Service indisponible", result.get("error"));
        mockServer.verify();
    }
}