package com.microservice_back.microservice_back.integration;



import com.fasterxml.jackson.databind.ObjectMapper;
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



import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    void shouldReturnEmptyListInitially() throws Exception {
        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void shouldCreateAndRetrievePatient() throws Exception {
        Patient patient = new Patient();
        patient.setNom("Dupont");
        patient.setPrenom("Jean");
        patient.setDateNaissance("1990-01-01");
        patient.setGenre(Genre.HOMME);
        patient.setAdresse("10 rue de Paris");
        patient.setTelephone("0102030405");

        mockMvc.perform(post("/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Dupont"));

        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Dupont"));
    }
    @Test
    void shouldDeletePatient() throws Exception {
        Patient patient = new Patient();
        patient.setPrenom("Jane");
        patient.setNom("Smith");
        patient.setDateNaissance("1985-05-05");
        patient.setGenre(Genre.FEMME);
        patient.setAdresse("5 rue Victor Hugo");
        patient.setTelephone("0611111111");

        Patient saved = patientRepository.save(patient);

        mockMvc.perform(delete("/patients/{id}", saved.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
