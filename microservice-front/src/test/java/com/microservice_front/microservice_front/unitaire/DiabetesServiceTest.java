package com.microservice_front.microservice_front.unitaire;

import com.medilabo.front.service.DiabetesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DiabetesServiceTest {

    private RestTemplate mockRestTemplate;
    private DiabetesService diabetesService;


    @BeforeEach
    void setup() {
        mockRestTemplate = Mockito.mock(RestTemplate.class);

    }

    @Test
    void getDiabetesReport_success() {

        Long patientId = 5L;
        String expectedUrl = "http://gatewayUrl/diabetes-service/assess/5";

        Map<String, Object> fakeResponse = Map.of(
                "age", 52,
                "nombreDeclencheurs", 4,
                "niveauRisque", "Borderline"
        );

        when(mockRestTemplate.getForObject(eq(expectedUrl), eq(Map.class)))
                .thenReturn(fakeResponse);

        Map result = diabetesService.getDiabetesReport(patientId);

        assertNotNull(result);
        assertEquals(52, result.get("age"));
        assertEquals(4, result.get("nombreDeclencheurs"));
        assertEquals("Borderline", result.get("niveauRisque"));

        verify(mockRestTemplate, times(1))
                .getForObject(expectedUrl, Map.class);
    }

    @Test
    void getDiabetesReport_error_returnsNull() {

        Long patientId = 8L;
        String expectedUrl = "http://gatewayUrl/diabetes-service/assess/8";

        when(mockRestTemplate.getForObject(eq(expectedUrl), eq(Map.class)))
                .thenThrow(new RuntimeException("Erreur API"));

        Map result = diabetesService.getDiabetesReport(patientId);

        assertNull(result);

        verify(mockRestTemplate, times(1))
                .getForObject(expectedUrl, Map.class);
    }

    //@Test
   // void baseUrl_isConstructedCorrectly() {



      //  Map fake = Map.of("ok", true);

      //  when(mockRestTemplate.getForObject(
       //         eq("http://myGateway/diabetes-service/assess/1"), eq(Map.class)))
      //          .thenReturn(fake);

        //Map result = service.getDiabetesReport(1L);

       // assertEquals(fake, result);
   // }
}
