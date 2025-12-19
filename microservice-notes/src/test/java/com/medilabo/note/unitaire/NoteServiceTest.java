package com.medilabo.note.unitaire;

import com.medilabo.note.model.Note;
import com.medilabo.note.repository.NoteRepository;
import com.medilabo.note.service.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository repository;

    @InjectMocks
    private NoteService noteService;

    private Note sampleNote;

    @BeforeEach
    void setUp() {
        sampleNote = new Note();
        sampleNote.setId("6582f1b2c9e123456789abcd"); // Format ID MongoDB typique
        sampleNote.setPatId(1L);
        sampleNote.setNote("Le patient signale des douleurs articulaires.");
    }

    @Test
    void testGetAllNotes() {
        // Arrange
        when(repository.findAll()).thenReturn(Arrays.asList(sampleNote));

        // Act
        List<Note> result = noteService.getAllNotes();

        // Assert
        assertEquals(1, result.size());
        assertEquals("6582f1b2c9e123456789abcd", result.get(0).getId());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testGetNotesByPatient() {
        // Arrange
        when(repository.findByPatId(1L)).thenReturn(Arrays.asList(sampleNote));

        // Act
        List<Note> result = noteService.getNotesByPatient(1L);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.get(0).getPatId());
        verify(repository, times(1)).findByPatId(1L);
    }

    @Test
    void testSaveNote() {
        // Arrange
        when(repository.save(any(Note.class))).thenReturn(sampleNote);

        // Act
        Note savedNote = noteService.save(new Note());

        // Assert
        assertNotNull(savedNote);
        assertEquals("6582f1b2c9e123456789abcd", savedNote.getId());
        verify(repository, times(1)).save(any(Note.class));
    }

    @Test
    void testUpdateNote() {
        // Arrange
        String targetId = "new-id-123";
        Note noteDetails = new Note();
        noteDetails.setNote("Note mise à jour");

        // Mocking save to return the object with the new ID
        when(repository.save(any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Note result = noteService.update(targetId, noteDetails);

        // Assert
        assertEquals(targetId, result.getId());
        assertEquals("Note mise à jour", result.getNote());
        verify(repository).save(any(Note.class));
    }

    @Test
    void testDeleteNote() {
        // Arrange
        String idToDelete = "6582f1b2c9e123456789abcd";
        doNothing().when(repository).deleteById(idToDelete);

        // Act
        noteService.delete(idToDelete);

        // Assert
        verify(repository, times(1)).deleteById(idToDelete);
    }
}
