package com.medilabo.diabetes.unitaire;

import com.medilabo.diabetes.model.Patient;
import com.medilabo.diabetes.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(PatientService.class)
class PatientServiceTest {

    @Autowired
    private PatientService patientService;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void testGetAllPatients() {
        // Arrange : Simulation d'une réponse JSON contenant une liste de patients
        String jsonResponse = "[{\"id\":1, \"prenom\":\"Jean\", \"nom\":\"Dupont\"}]";

        this.server.expect(requestTo("http://localhost:8083/patient-service/patients"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        // Act
        List<Patient> patients = patientService.getAllPatients();

        // Assert
        assertNotNull(patients);
        assertEquals(1, patients.size());
        assertEquals("Jean", patients.get(0).getPrenom());
    }

    @Test
    void testGetPatientById() {
        // Arrange
        String jsonResponse = "{\"id\":1, \"prenom\":\"Jean\", \"nom\":\"Dupont\"}";

        this.server.expect(requestTo("http://localhost:8083/patient-service/patients/1"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        // Act
        Patient patient = patientService.getPatientById(1L);

        // Assert
        assertNotNull(patient);
        assertEquals("Dupont", patient.getNom());
    }

    @Test
    void testUpdatePatient() {
        // Arrange
        Patient patientToUpdate = new Patient();
        patientToUpdate.setId(1L);
        patientToUpdate.setPrenom("Jean-Updated");

        this.server.expect(requestTo("http://localhost:8083/patient-service/patients/1"))
                .andExpect(method(org.springframework.http.HttpMethod.PUT))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess());

        // Act & Assert
        assertDoesNotThrow(() -> patientService.updatePatient(1L, patientToUpdate));
    }
}