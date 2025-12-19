package com.medilabo.diabetes.integration;

import com.medilabo.diabetes.controller.DiabetesController;
import com.medilabo.diabetes.model.Note;
import com.medilabo.diabetes.model.Patient;
import com.medilabo.diabetes.repository.NoteRepository;
import com.medilabo.diabetes.repository.PatientRepository;
import com.medilabo.diabetes.service.DiabetesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DiabetesController.class)
@AutoConfigureMockMvc(addFilters = false) // Désactive la sécurité pour le test
class DiabetesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DiabetesService diabetesService;

    @MockitoBean
    private PatientRepository patientRepository;

    @MockitoBean
    private NoteRepository noteRepository;

    private Patient mockPatient;
    private List<Note> mockNotes;

    @BeforeEach
    void setUp() {
        // Préparation d'un patient de test
        mockPatient = new Patient();
        mockPatient.setId(1L);
        mockPatient.setPrenom("Test");
        mockPatient.setNom("Patient");
        mockPatient.setGenre("M");
        mockPatient.setDateNaissance("1980-01-01");

        // Préparation d'une note de test
        Note note = new Note();
        note.setNote("Le patient présente des signes de Diabète");
        mockNotes = Collections.singletonList(note);
    }

    @Test
    void testGetAssessment_Success() throws Exception {
        // Simulation des appels aux repositories
        when(patientRepository.getPatientById(1L)).thenReturn(mockPatient);
        when(noteRepository.getNotesByPatient(1L)).thenReturn(mockNotes);

        // Simulation de la logique du service
        when(diabetesService.countTriggers(anyList())).thenReturn(2);
        when(diabetesService.calculateAge(any(LocalDate.class))).thenReturn(45);
        when(diabetesService.determineRiskLevel(anyString(), anyInt(), anyInt())).thenReturn("Borderline");

        // Exécution et vérification
        mockMvc.perform(get("/diabetes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value(1))
                .andExpect(jsonPath("$.prenom").value("Test"))
                .andExpect(jsonPath("$.nombreDeclencheurs").value(2))
                .andExpect(jsonPath("$.niveauRisque").value("Borderline"))
                .andExpect(jsonPath("$.age").value(45));
    }
}