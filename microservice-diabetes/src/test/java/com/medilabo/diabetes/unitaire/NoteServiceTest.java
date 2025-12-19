package com.medilabo.diabetes.unitaire;

import com.medilabo.diabetes.model.Note;
import com.medilabo.diabetes.service.NoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


@RestClientTest(NoteService.class)
class NoteServiceTest {

    @Autowired
    private NoteService noteService;

    @Autowired
    private MockRestServiceServer server;

    @Test
    void testGetNotesByPatient() {
        // Arrange
        String jsonResponse = "[{\"id\":\"1\", \"patId\":1, \"note\":\"Test Note\"}]";

        this.server.expect(requestTo("http://localhost:8083/note-service/notes/patient/1"))
                .andExpect(method(org.springframework.http.HttpMethod.GET))
                .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        // Act
        List<Note> notes = noteService.getNotesByPatient(1L);

        // Assert
        assertNotNull(notes);
        assertEquals(1, notes.size());
        assertEquals("Test Note", notes.get(0).getNote());
    }

    @Test
    void testSaveNote() {
        // Arrange
        Note noteToSave = new Note();
        noteToSave.setPatId(1L);
        noteToSave.setNote("New note");

        this.server.expect(requestTo("http://localhost:8083/note-service/notes"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess());

        // Act & Assert
        assertDoesNotThrow(() -> noteService.save(noteToSave));
    }

    @Test
    void testDeleteNote() {
        // Arrange
        String noteId = "abc-123";
        this.server.expect(requestTo("http://localhost:8083/note-service/notes/" + noteId))
                .andExpect(method(org.springframework.http.HttpMethod.DELETE))
                .andRespond(withSuccess());

        // Act & Assert
        assertDoesNotThrow(() -> noteService.delete(noteId));
    }
}
