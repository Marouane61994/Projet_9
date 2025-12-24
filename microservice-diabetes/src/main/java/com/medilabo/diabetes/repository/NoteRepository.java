package com.medilabo.diabetes.repository;

import com.medilabo.diabetes.model.Note;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Repository
public class NoteRepository {

    private final RestClient restClient;

    public NoteRepository(@Qualifier("noteRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public List<Note> getNotesByPatient(Long patId) {
        Note[] notes = restClient.get()
                .uri("/notes/patient/{patId}", patId)
                .retrieve()
                .body(Note[].class);

        return Arrays.asList(Objects.requireNonNullElse(notes, new Note[0]));
    }
}
