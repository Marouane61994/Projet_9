package com.microservice_back.microservice_back.integration;

import com.medilabo.patient.model.Genre;
import com.medilabo.patient.model.Patient;
import com.medilabo.patient.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        patientRepository.deleteAll();
    }

    @Test
    void testCreatePatient() throws Exception {
        Patient patient = new Patient(null, "Doe", "John", "1990-01-01", Genre.HOMME, "Rue X", "0102030405");

        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Doe"))
                .andExpect(jsonPath("$.prenom").value("John"));
    }

    @Test
    void testGetAllPatients() throws Exception {
        Patient p1 = patientRepository.save(new Patient(null, "Doe", "John", "1990-01-01", Genre.HOMME, "Rue X", "0102030405"));
        Patient p2 = patientRepository.save(new Patient(null, "Smith", "Jane", "1985-05-10", Genre.FEMME, "Rue Y", "0607080910"));

        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nom").value("Doe"))
                .andExpect(jsonPath("$[1].nom").value("Smith"));
    }

    @Test
    void testGetPatientById() throws Exception {
        Patient patient = patientRepository.save(new Patient(null, "Doe", "John", "1990-01-01", Genre.HOMME, "Rue X", "0102030405"));

        mockMvc.perform(get("/patients/{id}", patient.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Doe"))
                .andExpect(jsonPath("$.prenom").value("John"));
    }

    @Test
    void testUpdatePatient() throws Exception {
        Patient patient = patientRepository.save(new Patient(null, "Doe", "John", "1990-01-01", Genre.HOMME, "Rue X", "0102030405"));

        patient.setNom("UpdatedDoe");
        patient.setPrenom("UpdatedJohn");

        mockMvc.perform(put("/patients/{id}", patient.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("UpdatedDoe"))
                .andExpect(jsonPath("$.prenom").value("UpdatedJohn"));
    }
}
