package com.microservice_front.microservice_front.integration;

import com.medilabo.front.Controller.PatientController;
import com.medilabo.front.Service.NoteService;
import com.medilabo.front.Service.PatientService;
import com.medilabo.front.model.Note;
import com.medilabo.front.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.test.web.servlet.MockMvc;



import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;


    private PatientService patientService;


    private NoteService noteService;

    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setNom("John");
        patient.setPrenom("Doe");
        patient.setGenre("M");
        patient.setAdresse("123 Rue Test");
        patient.setTelephone("0102030405");
    }

    //  Test : afficher la liste des patients
    @Test
    void testListPatients() throws Exception {
        List<Patient> patients = Collections.singletonList(patient);
        Mockito.when(patientService.getAllPatients()).thenReturn(patients);

        mockMvc.perform(get("/patients"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients"))
                .andExpect(model().attributeExists("patients"))
                .andExpect(model().attribute("patients", patients));
    }

    //  Test : afficher la liste avec message de succès
    @Test
    void testListPatientsWithSuccessMessage() throws Exception {
        Mockito.when(patientService.getAllPatients()).thenReturn(Collections.singletonList(patient));

        mockMvc.perform(get("/patients").param("success", "ok"))
                .andExpect(status().isOk())
                .andExpect(view().name("patients"))
                .andExpect(model().attributeExists("successMessage"))
                .andExpect(model().attribute("successMessage", "ok"));
    }

    //  Test : afficher les détails d’un patient
    @Test
    void testGetPatientDetails() throws Exception {
        Note note = new Note();
        note.setId("n1");
        note.setPatId(1L);
        note.setNote("Patient en bonne santé.");

        Mockito.when(patientService.getPatientById(1L)).thenReturn(patient);
        Mockito.when(noteService.getNotesByPatient(1L)).thenReturn(Collections.singletonList(note));

        mockMvc.perform(get("/patients/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("patient-details"))
                .andExpect(model().attributeExists("patient"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("patient", patient));
    }

    //  Test : afficher le formulaire d’édition
    @Test
    void testShowEditForm() throws Exception {
        Mockito.when(patientService.getPatientById(1L)).thenReturn(patient);

        mockMvc.perform(get("/patients/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("patient-edit"))
                .andExpect(model().attributeExists("patient"))
                .andExpect(model().attribute("patient", patient));
    }

    //  Test : soumettre la modification d’un patient
    @Test
    void testUpdatePatient() throws Exception {
        Mockito.doNothing().when(patientService).updatePatient(eq(1L), any(Patient.class));

        mockMvc.perform(post("/patients/1/edit")
                        .param("firstName", "Jane")
                        .param("lastName", "Doe")
                        .param("gender", "F")
                        .param("address", "456 Avenue Test")
                        .param("phone", "0606060606"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients?success=Le+patient+a+bien+été+modifié+%21"));

        Mockito.verify(patientService).updatePatient(eq(1L), any(Patient.class));
    }
}
