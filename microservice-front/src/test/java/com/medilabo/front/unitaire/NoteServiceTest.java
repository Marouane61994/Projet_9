package com.medilabo.front.unitaire;

import com.medilabo.front.model.Note;
import com.medilabo.front.service.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    private NoteService noteService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RestClient restClient;

    @BeforeEach
    void setUp() throws Exception {

        noteService = new NoteService(
                RestClient.builder(),
                "http://localhost:8083",
                "user",
                "pass"
        );


        Field field = NoteService.class.getDeclaredField("restClient");
        field.setAccessible(true);
        field.set(noteService, restClient);
    }

    @Test
    void testGetNotesByPatient() {
        // Arrange : Création d'un objet Note avec des Setters
        Note mockNote = new Note();
        mockNote.setPatId(1L);
        mockNote.setNote("Test Note Content");

        Note[] mockNotesArray = { mockNote };

        when(restClient.get()
                .uri(anyString(), anyLong())
                .retrieve()
                .body(Note[].class))
                .thenReturn(mockNotesArray);

        // Act
        List<Note> result = noteService.getNotesByPatient(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Note Content", result.get(0).getNote());
    }

    @Test
    void testSaveNote() {
        // Arrange
        Note noteToSave = new Note();
        noteToSave.setPatId(1L);
        noteToSave.setNote("New Note");

        when(restClient.post()
                .uri(anyString())
                .body(any(Note.class))
                .retrieve()
                .body(Note.class))
                .thenReturn(noteToSave);

        // Act
        noteService.save(noteToSave);

        // Assert
        assertDoesNotThrow(() -> noteService.save(noteToSave));
    }

    @Test
    void testDeleteNote() {
        // Arrange
        when(restClient.delete()
                .uri(anyString(), anyString())
                .retrieve()
                .toBodilessEntity())
                .thenReturn(null);

        // Act
        noteService.delete("some-id");

        // Assert
        assertDoesNotThrow(() -> noteService.delete("some-id"));
    }
}