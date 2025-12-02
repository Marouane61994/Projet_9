package com.microservice_back.microservice_back.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo.patient.PatientApplication;
import com.medilabo.patient.model.Genre;
import com.medilabo.patient.model.Patient;
import com.medilabo.patient.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = PatientApplication.class)
@AutoConfigureMockMvc
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private Patient savedPatient;

    @BeforeEach
    void setup() {
        repository.deleteAll();

        savedPatient = repository.save(new Patient(
                null,
                "Doe",              // nom
                "John",             // prenom
                "1980-01-01",       // dateNaissance (String)
                Genre.HOMME,        // enum Genre
                "10 Main Street",
                "3000000000"
        ));
    }

    @Test
    @DisplayName("GET /patients doit retourner la liste des patients")
    void getAllPatients_shouldReturnList() throws Exception {
        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Doe"))
                .andExpect(jsonPath("$[0].prenom").value("John"));
    }

    @Test
    @DisplayName("GET /patients/{id} doit retourner un patient")
    void getPatientById_shouldReturnPatient() throws Exception {
        mockMvc.perform(get("/patients/" + savedPatient.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Doe"))
                .andExpect(jsonPath("$.prenom").value("John"));
    }

    @Test
    @DisplayName("POST /patients doit créer un patient")
    void createPatient_shouldCreateNewPatient() throws Exception {

        Patient newPatient = new Patient(
                null,
                "Smith",
                "Alice",
                "1990-05-15",
                Genre.FEMME,
                "5 Sunset Blvd",
                "4000000000"
        );

        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPatient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Smith"))
                .andExpect(jsonPath("$.prenom").value("Alice"));
    }

    @Test
    @DisplayName("PUT /patients/{id} doit mettre à jour un patient")
    void updatePatient_shouldUpdateExistingPatient() throws Exception {

        Patient update = new Patient(
                null,
                "Updated",
                "Johnny",
                "1980-01-01",
                Genre.HOMME,
                "New Street",
                "3100000000"
        );

        mockMvc.perform(put("/patients/" + savedPatient.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Updated"))
                .andExpect(jsonPath("$.prenom").value("Johnny"));
    }

}
