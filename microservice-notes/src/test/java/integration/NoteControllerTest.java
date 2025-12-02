package integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo.note.controller.NoteController;
import com.medilabo.note.model.Note;
import com.medilabo.note.service.NoteService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoteController.class)
@ContextConfiguration(classes = com.medilabo.note.NoteApplication.class)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NoteService service;

    @Test
    void getAllNotes_shouldReturnList() throws Exception {
        List<Note> notes = Arrays.asList(
                new Note("1", 1L, "Jean Dupont", "Première note"),
                new Note("2", 1L, "Jean Dupont", "Deuxième note")
        );

        Mockito.when(service.getAllNotes()).thenReturn(notes);

        mockMvc.perform(get("/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].note").value("Première note"));
    }

    @Test
    void getNotesByPatient_shouldReturnNotes() throws Exception {
        List<Note> notes = List.of(
                new Note("1", 5L, "John Doe", "Note patient 5")
        );

        Mockito.when(service.getNotesByPatient(5L)).thenReturn(notes);

        mockMvc.perform(get("/notes/patient/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].patient").value("John Doe"));
    }

    @Test
    void createNote_shouldReturnCreatedNote() throws Exception {
        Note note = new Note(null, 2L, "Marie Curie", "Nouvelle note");
        Note saved = new Note("123", 2L, "Marie Curie", "Nouvelle note");

        Mockito.when(service.save(any(Note.class))).thenReturn(saved);

        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(note)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123"))
                .andExpect(jsonPath("$.note").value("Nouvelle note"));
    }

    @Test
    void updateNote_shouldReturnUpdatedNote() throws Exception {
        Note update = new Note(null, 3L, "Paul Martin", "Note mise à jour");
        Note updated = new Note("777", 3L, "Paul Martin", "Note mise à jour");

        Mockito.when(service.update(eq("777"), any(Note.class))).thenReturn(updated);

        mockMvc.perform(put("/notes/777")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("777"))
                .andExpect(jsonPath("$.note").value("Note mise à jour"));
    }

    @Test
    void deleteNote_shouldReturnOk() throws Exception {
        Mockito.doNothing().when(service).delete("999");

        mockMvc.perform(delete("/notes/999"))
                .andExpect(status().isOk());
    }
}
