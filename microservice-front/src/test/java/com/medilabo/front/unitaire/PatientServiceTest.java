package com.medilabo.front.unitaire;

import com.medilabo.front.model.Patient;
import com.medilabo.front.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    private PatientService patientService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RestClient restClient;

    @BeforeEach
    void setUp() throws Exception {
        patientService = new PatientService(
                RestClient.builder(),
                "http://localhost:8083",
                "admin",
                "password"
        );

        Field field = PatientService.class.getDeclaredField("restClient");
        field.setAccessible(true);
        field.set(patientService, restClient);
    }

    @Test
    void testGetAllPatients() {
        // Arrange
        Patient p1 = new Patient();
        p1.setNom("Doe");
        Patient[] mockArray = { p1 };

        when(restClient.get()
                .uri("/patients")
                .retrieve()
                .body(Patient[].class))
                .thenReturn(mockArray);

        // Act
        List<Patient> result = patientService.getAllPatients();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Doe", result.get(0).getNom());
    }

    @Test
    void testGetPatientById() {
        // Arrange
        Patient mockPatient = new Patient();
        mockPatient.setId(1L);
        mockPatient.setNom("Smith");

        when(restClient.get()
                .uri(anyString(), anyLong())
                .retrieve()
                .body(Patient.class))
                .thenReturn(mockPatient);

        // Act
        Patient result = patientService.getPatientById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Smith", result.getNom());
    }

    @Test
    void testUpdatePatient() {
        // Arrange
        Patient patientToUpdate = new Patient();
        patientToUpdate.setNom("UpdatedName");
        when(restClient.put()
                .uri(anyString(), anyLong())
                .body(any(Patient.class))
                .retrieve()
                .toBodilessEntity())
                .thenReturn(null);

        // Act & Assert
        assertDoesNotThrow(() -> patientService.updatePatient(1L, patientToUpdate));
    }
}