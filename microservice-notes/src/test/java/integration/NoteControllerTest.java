package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo.note.controller.NoteController;
import com.medilabo.note.model.Note;
import com.medilabo.note.service.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;


import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoteController.class)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;


    private NoteService noteService;

    @Autowired
    private ObjectMapper objectMapper;

    private Note note1;
    private Note note2;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        note1 = new Note();
        note1.setId("1");
        note1.setPatId(100L);
        note1.setNote("Note 1");

        note2 = new Note();
        note2.setId("2");
        note2.setPatId(200L);
        note2.setNote("Note 2");
    }


    @Test
    @DisplayName("GET /notes - doit retourner la liste de toutes les notes")
    void getAllNotes_shouldReturnListOfNotes() throws Exception {
        Mockito.when(noteService.getAllNotes()).thenReturn(List.of(note1, note2));

        mockMvc.perform(get("/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].patId", is(101)))
                .andExpect(jsonPath("$[1].note", is("Deuxième note")));
    }

    @Test
    @DisplayName("GET /notes/patient/{patId} - doit retourner les notes d’un patient")
    void getNotesByPatient_shouldReturnNotesForPatient() throws Exception {
        Mockito.when(noteService.getNotesByPatient(101L)).thenReturn(List.of(note1));

        mockMvc.perform(get("/notes/patient/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].note", is("Première note")))
                .andExpect(jsonPath("$[0].patId", is(101)));
    }

    @Test
    @DisplayName("POST /notes - doit créer une nouvelle note")
    void createNote_shouldReturnCreatedNote() throws Exception {
        Note toCreate = new Note();
        toCreate.setId(null);
        toCreate.setPatId(103L);
        toCreate.setNote("Nouvelle note");

        Note created = new Note();
        created.setId("3");
        created.setPatId(103L);
        created.setNote("Nouvelle note");


        Mockito.when(noteService.save(any(Note.class))).thenReturn(created);

        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toCreate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("3")))
                .andExpect(jsonPath("$.patId", is(103)))
                .andExpect(jsonPath("$.note", is("Nouvelle note")));
    }

    @Test
    @DisplayName("PUT /notes/{id} - doit mettre à jour une note existante")
    void updateNote_shouldReturnUpdatedNote() throws Exception {

        Note updated = new Note();
        updated.setId("1");
        updated.setPatId(101L);
        updated.setNote("Note mise à jour");

        Mockito.when(noteService.update(eq("1"), any(Note.class))).thenReturn(updated);

        mockMvc.perform(put("/notes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.note", is("Note mise à jour")));
    }

    @Test
    @DisplayName("DELETE /notes/{id} - doit supprimer une note")
    void deleteNote_shouldReturnOk() throws Exception {
        mockMvc.perform(delete("/notes/1"))
                .andExpect(status().isOk());

        Mockito.verify(noteService).delete("1");
    }
}
