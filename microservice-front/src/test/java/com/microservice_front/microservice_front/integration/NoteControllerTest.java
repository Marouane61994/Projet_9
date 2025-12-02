package com.microservice_front.microservice_front.integration;

import com.medilabo.front.Controller.NoteController;
import com.medilabo.front.Service.NoteService;
import com.medilabo.front.Service.PatientService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration(classes = com.medilabo.front.FrontApplication.class)
@WebMvcTest(NoteController.class)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NoteService noteService;

    @MockitoBean
    private PatientService patientService;

    // -------------------------------------------
    // TEST : GET /notes/patient/{patId}
    // -------------------------------------------
    @Test
    void getNotesByPatient_shouldReturnPatientDetailsView() throws Exception {

        Patient patient = new Patient(
                1L,
                "Doe",
                "John",
                "1980-01-01",
                "M",
                "10 Rue de Paris",
                "0102030405"
        );


        Note n1 = new Note();
        n1.setId("a1");
        n1.setPatId(1L);
        n1.setPatient("John Doe");
        n1.setNote("Première note");

        Note n2 = new Note();
        n2.setId("b2");
        n2.setPatId(1L);
        n2.setPatient("John Doe");
        n2.setNote("Deuxième note");

        List<Note> notes = List.of(n1, n2);

        when(patientService.getPatientById(1L)).thenReturn(patient);
        when(noteService.getNotesByPatient(1L)).thenReturn(notes);

        mockMvc.perform(get("/notes/patient/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("patient-details"))
                .andExpect(model().attributeExists("patient"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("patient", patient))
                .andExpect(model().attribute("notes", notes));
    }

    // -------------------------------------------
    // TEST : POST /notes (ajout d'une note)
    // -------------------------------------------
    @Test
    void addNote_shouldRedirectToPatientDetails() throws Exception {

        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("patId", "2")
                        .param("patient", "Marie Dupont")
                        .param("note", "Nouvelle note importante"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients/2"));

        verify(noteService, times(1)).save(any(Note.class));
    }

    // -------------------------------------------
    // TEST : DELETE /notes/{id}
    // -------------------------------------------
    @Test
    void deleteNote_shouldReturnOk() throws Exception {

        doNothing().when(noteService).delete("xyz");

        mockMvc.perform(delete("/notes/xyz"))
                .andExpect(status().isOk());

        verify(noteService, times(1)).delete("xyz");
    }
}
