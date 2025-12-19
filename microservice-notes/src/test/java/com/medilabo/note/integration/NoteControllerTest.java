package com.medilabo.note.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.medilabo.note.controller.NoteController;
import com.medilabo.note.model.Note;
import com.medilabo.note.service.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoteController.class)
@AutoConfigureMockMvc(addFilters = false)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NoteService noteService;

    @Autowired
    private ObjectMapper objectMapper;

    private Note note;

    @BeforeEach
    void setUp() {
        note = new Note();
        note.setId("mongo-id-123");
        note.setPatId(1L);
        note.setNote("Le patient présente des symptômes de vertige.");
    }

    @Test
    void testGetAllNotes() throws Exception {
        when(noteService.getAllNotes()).thenReturn(Collections.singletonList(note));

        mockMvc.perform(get("/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value("mongo-id-123"));
    }

    @Test
    void testGetNotesByPatient() throws Exception {
        when(noteService.getNotesByPatient(1L)).thenReturn(Arrays.asList(note));

        mockMvc.perform(get("/notes/patient/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].patId").value(1));
    }

    @Test
    void testCreateNote() throws Exception {
        when(noteService.save(any(Note.class))).thenReturn(note);

        mockMvc.perform(post("/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(note)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.note").value("Le patient présente des symptômes de vertige."));
    }

    @Test
    void testUpdateNote() throws Exception {
        when(noteService.update(eq("mongo-id-123"), any(Note.class))).thenReturn(note);

        mockMvc.perform(put("/notes/mongo-id-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(note)))
                .andExpect(status().isOk());
    }

    @Test
    void testDeleteNote() throws Exception {
        doNothing().when(noteService).delete("mongo-id-123");

        mockMvc.perform(delete("/notes/mongo-id-123"))
                .andExpect(status().isOk());
    }
}