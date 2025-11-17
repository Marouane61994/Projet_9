package unitaire;

import com.medilabo.note.model.Note;
import com.medilabo.note.repository.NoteRepository;
import com.medilabo.note.service.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    private Note note1;
    private Note note2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        note1 = new Note();
        note1.setPatId(Long.valueOf("1"));
        note1.setPatient("Martin");
        note1.setNote("Note 1");



        note2 = new Note("2", 200L, "Note 2");
    }

    @Test
    @DisplayName("getAllNotes() doit retourner toutes les notes")
    void getAllNotes_shouldReturnAllNotes() {
        when(noteRepository.findAll()).thenReturn(List.of(note1, note2));

        List<Note> result = noteService.getAllNotes();

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(note1, note2);
        verify(noteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getNotesByPatient() doit retourner les notes d’un patient donné")
    void getNotesByPatient_shouldReturnNotesForPatient() {
        when(noteRepository.findByPatId(100)).thenReturn(List.of(note1));

        List<Note> result = noteService.getNotesByPatient(100L);

        assertThat(result).containsExactly(note1);
        verify(noteRepository, times(1)).findByPatId(100);
    }

    @Test
    @DisplayName("save() doit enregistrer une note et la retourner")
    void save_shouldReturnSavedNote() {
        when(noteRepository.save(note1)).thenReturn(note1);

        Note result = noteService.save(note1);

        assertThat(result).isEqualTo(note1);
        verify(noteRepository, times(1)).save(note1);
    }

    @Test
    @DisplayName("update() doit mettre à jour une note existante")
    void update_shouldReturnUpdatedNote() {
        Note updated = new Note("1", 100L, "Note mise à jour");
        when(noteRepository.save(updated)).thenReturn(updated);

        Note result = noteService.update("1", updated);

        assertThat(result.getId()).isEqualTo("1");
        assertThat(result.getNote()).isEqualTo("Note mise à jour");
        verify(noteRepository, times(1)).save(updated);
    }

    @Test
    @DisplayName("delete() doit supprimer une note par ID")
    void delete_shouldCallRepositoryDeleteById() {
        doNothing().when(noteRepository).deleteById("1");

        noteService.delete("1");

        verify(noteRepository, times(1)).deleteById("1");
    }
}
