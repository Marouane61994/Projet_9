package com.microservice_front.microservice_front.integration;

import com.medilabo.front.controller.PatientController;
import com.medilabo.front.service.DiabetesService;
import com.medilabo.front.service.NoteService;
import com.medilabo.front.service.PatientService;
import com.medilabo.front.model.Note;
import com.medilabo.front.model.Patient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration(classes = com.medilabo.front.FrontApplication.class)
@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService patientService;

    @MockitoBean
    private NoteService noteService;

    @MockitoBean
    private DiabetesService diabetesService;

    // ----------------------------------------------------------
    // GET /patients
    // ----------------------------------------------------------
    @Test
    void listPatients_shouldReturnPatientsView() throws Exception {

        List<Patient> patients = List.of(new Patient(1L, "Doe", "John", "1980-01-01", "M", "10 rue X", "0102030405"), new Patient(2L, "Smith", "Anna", "1990-02-02", "F", "20 rue Y", "0607080910"));

        when(patientService.getAllPatients()).thenReturn(patients);

        mockMvc.perform(get("/patients")).andExpect(status().isOk()).andExpect(view().name("patients")).andExpect(model().attributeExists("patients")).andExpect(model().attribute("patients", patients));
    }

    // ----------------------------------------------------------
    // GET /patients/{id}
    // ----------------------------------------------------------
    @Test
    void getPatientDetails_shouldReturnPatientDetailsView() throws Exception {

        Patient patient = new Patient(1L, "Doe", "John", "1980-01-01", "M", "10 rue X", "0102030405");

        List<Note> notes = List.of(new Note("a1", 1L, "John Doe", "Première note"), new Note("b2", 1L, "John Doe", "Deuxième note"));

        Map<String, Object> rapport = Map.of("age", 45, "nombreDeclencheurs", 6, "niveauRisque", "EarlyOnset");

        when(patientService.getPatientById(1L)).thenReturn(patient);
        when(noteService.getNotesByPatient(1L)).thenReturn(notes);
        when(diabetesService.getDiabetesReport(1L)).thenReturn(rapport);

        mockMvc.perform(get("/patients/1")).andExpect(status().isOk()).andExpect(view().name("patient-details")).andExpect(model().attribute("patient", patient)).andExpect(model().attribute("notes", notes)).andExpect(model().attribute("age", 45)).andExpect(model().attribute("triggerCount", 6)).andExpect(model().attribute("riskLevel", "EarlyOnset"));
    }

    // Rapport indisponible
    @Test
    void getPatientDetails_shouldHandleNullDiabetesReport() throws Exception {

        Patient patient = new Patient(1L, "Doe", "John", "1980-01-01", "M", "10 rue X", "0102030405");

        when(patientService.getPatientById(1L)).thenReturn(patient);
        when(noteService.getNotesByPatient(1L)).thenReturn(List.of());
        when(diabetesService.getDiabetesReport(1L)).thenReturn(null);

        mockMvc.perform(get("/patients/1")).andExpect(status().isOk()).andExpect(view().name("patient-details")).andExpect(model().attribute("age", "N/A")).andExpect(model().attribute("triggerCount", "N/A")).andExpect(model().attribute("riskLevel", "Indisponible"));
    }

    // ----------------------------------------------------------
    // GET /patients/{id}/edit
    // ----------------------------------------------------------
    @Test
    void showEditForm_shouldReturnEditView() throws Exception {

        Patient patient = new Patient(1L, "Doe", "John", "1980-01-01", "M", "10 rue X", "0102030405");

        when(patientService.getPatientById(1L)).thenReturn(patient);

        mockMvc.perform(get("/patients/1/edit")).andExpect(status().isOk()).andExpect(view().name("patient-edit")).andExpect(model().attribute("patient", patient));
    }

    // ----------------------------------------------------------
    // POST /patients/{id}/edit
    // ----------------------------------------------------------
    @Test
    void updatePatient_shouldRedirectWithSuccessMessage() throws Exception {

        doNothing().when(patientService).updatePatient(eq(1L), any(Patient.class));

        mockMvc.perform(post("/patients/1/edit").contentType(MediaType.APPLICATION_FORM_URLENCODED).param("nom", "Doe").param("prenom", "John").param("genre", "M").param("dateNaissance", "1980-01-01").param("adresse", "50 rue Z").param("telephone", "0101010101")).andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/patients?success=Le+patient+a+bien+%C3%A9t%C3%A9+modifi%C3%A9+%21"));

        verify(patientService, times(1)).updatePatient(eq(1L), any(Patient.class));
    }
}
