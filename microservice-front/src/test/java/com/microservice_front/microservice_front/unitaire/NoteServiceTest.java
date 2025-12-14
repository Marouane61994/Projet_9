package com.microservice_front.microservice_front.unitaire;

import com.medilabo.front.service.NoteService;
import com.medilabo.front.model.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class NoteServiceTest {

    private RestClient.Builder mockBuilder;
    private RestClient mockRestClient;

    private RestClient.RequestHeadersUriSpec mockRequestHeadersUriSpec;
    private RestClient.RequestBodyUriSpec mockRequestBodyUriSpec;

    private RestClient.ResponseSpec mockResponseSpec;

    private NoteService noteService;

    @BeforeEach
    void setup() {

        mockBuilder = mock(RestClient.Builder.class);
        mockRestClient = mock(RestClient.class);
        mockRequestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        mockRequestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        mockResponseSpec = mock(RestClient.ResponseSpec.class);

        when(mockBuilder.baseUrl(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.build()).thenReturn(mockRestClient);

        noteService = new NoteService(mockBuilder, "http://gateway");
    }

    // =============================
    //       GET NOTES BY PATIENT
    // =============================

    @Test
    void getNotesByPatient_success() {

        Long patId = 42L;

        Note note1 = new Note("a", patId, "John Doe", "Note A");
        Note note2 = new Note("b", patId, "John Doe", "Note B");

        Note[] notesArray = {note1, note2};

        when(mockRestClient.get()).thenReturn(mockRequestHeadersUriSpec);
        when(mockRequestHeadersUriSpec.uri("/notes/patient/{patId}", patId)).thenReturn(mockRequestHeadersUriSpec);
        when(mockRequestHeadersUriSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.body(Note[].class)).thenReturn(notesArray);

        List<Note> result = noteService.getNotesByPatient(patId);

        assertEquals(2, result.size());
        assertEquals("a", result.get(0).getId());
        assertEquals("b", result.get(1).getId());

        verify(mockRestClient).get();
        verify(mockRequestHeadersUriSpec).uri("/notes/patient/{patId}", patId);
        verify(mockRequestHeadersUriSpec).retrieve();
        verify(mockResponseSpec).body(Note[].class);
    }

    // =============================
    //             SAVE NOTE
    // =============================

    @Test
    void saveNote_success() {

        Note note = new Note();
        note.setId("n1");
        note.setPatId(20L);
        note.setNote("Test note"); // ✔️ correction

        when(mockRestClient.post()).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.uri("/notes")).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.body(note)).thenReturn(mockRequestBodyUriSpec);
        when(mockRequestBodyUriSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.body(Note.class)).thenReturn(note);

        noteService.save(note);

        verify(mockRestClient).post();
        verify(mockRequestBodyUriSpec).uri("/notes");
        verify(mockRequestBodyUriSpec).body(note);
        verify(mockRequestBodyUriSpec).retrieve();
        verify(mockResponseSpec).body(Note.class);
    }

    // =============================
    //           DELETE NOTE
    // =============================

    @Test
    void deleteNote_success() {

        String id = "abc123";

        when(mockRestClient.delete()).thenReturn(mockRequestHeadersUriSpec);
        when(mockRequestHeadersUriSpec.uri("/notes/{id}", id)).thenReturn(mockRequestHeadersUriSpec);
        when(mockRequestHeadersUriSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.toBodilessEntity()).thenReturn(null); // ✔️ indispensable

        noteService.delete(id);

        verify(mockRestClient).delete();
        verify(mockRequestHeadersUriSpec).uri("/notes/{id}", id);
        verify(mockRequestHeadersUriSpec).retrieve();
        verify(mockResponseSpec).toBodilessEntity();
    }
}
