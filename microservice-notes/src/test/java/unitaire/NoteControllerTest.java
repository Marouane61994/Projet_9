package unitaire;

import com.medilabo.note.model.Note;
import com.medilabo.note.repository.NoteRepository;
import com.medilabo.note.service.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NoteServiceTest {

    @Mock
    private NoteRepository repository;

    @InjectMocks
    private NoteService service;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllNotes_shouldReturnList() {
        List<Note> notes = List.of(
                new Note("1", 1L, "Jean Dupont", "Note A"),
                new Note("2", 2L, "Marie Curie", "Note B")
        );

        when(repository.findAll()).thenReturn(notes);

        List<Note> result = service.getAllNotes();

        assertEquals(2, result.size());
        assertEquals("Jean Dupont", result.get(0).getPatient());
        verify(repository, times(1)).findAll();
    }

    @Test
    void getNotesByPatient_shouldReturnNotes() {
        List<Note> notes = List.of(
                new Note("1", 5L, "John Doe", "Note 1")
        );

        when(repository.findByPatId(5L)).thenReturn(notes);

        List<Note> result = service.getNotesByPatient(5L);

        assertEquals(1, result.size());
        assertEquals(5L, result.get(0).getPatId());
        verify(repository).findByPatId(5L);
    }

    @Test
    void save_shouldReturnSavedNote() {
        Note note = new Note(null, 3L, "Alice", "Nouvelle note");
        Note saved = new Note("abc123", 3L, "Alice", "Nouvelle note");

        when(repository.save(note)).thenReturn(saved);

        Note result = service.save(note);

        assertNotNull(result.getId());
        assertEquals("abc123", result.getId());
        verify(repository).save(note);
    }

    @Test
    void update_shouldSetIdAndSave() {
        Note note = new Note(null, 10L, "Bob", "Texte update");
        Note updated = new Note("777", 10L, "Bob", "Texte update");

        when(repository.save(note)).thenReturn(updated);

        Note result = service.update("777", note);

        assertEquals("777", note.getId()); // vérifie bien que le service set l'ID
        assertEquals("777", result.getId());
        verify(repository).save(note);
    }

    @Test
    void delete_shouldCallRepository() {
        doNothing().when(repository).deleteById("999");

        service.delete("999");

        verify(repository, times(1)).deleteById("999");
    }
}
