package com.medilabo.diabetes.repository;

import com.medilabo.diabetes.model.Note;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class NoteRepository {

    private final RestClient restClient;

    public NoteRepository(RestClient restClient) {
        this.restClient = restClient;
    }

    public List<Note> getNotesByPatient(Long patId) {
        Note[] notes = restClient.get()
                .uri("/note-service/notes/patient/{patId}", patId)
                .retrieve()
                .body(Note[].class);
        return Arrays.asList(Objects.requireNonNullElse(notes, new Note[0]));
    }
}
