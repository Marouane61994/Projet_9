package com.medilabo.diabetes.service;

import com.medilabo.diabetes.model.Note;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Service
public class NoteService {

    private final RestClient restClient;

    public NoteService(@Qualifier("noteRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public List<Note> getNotesByPatient(Long patId) {
        Note[] notes = restClient.get()
                // L'URI est relative à la baseUrl définie dans RestClientConfig
                .uri("/notes/patient/{patId}", patId)
                .retrieve()
                .body(Note[].class);

        return notes != null ? Arrays.asList(notes) : List.of();
    }

    public void save(Note note) {
        restClient.post()
                .uri("/notes")
                .body(note)
                .retrieve()
                .toBodilessEntity();
    }

    public void delete(String id) {
        restClient.delete()
                .uri("/notes/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}