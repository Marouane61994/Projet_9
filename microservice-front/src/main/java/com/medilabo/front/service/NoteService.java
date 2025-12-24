package com.medilabo.front.service;

import com.medilabo.front.model.Note;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class NoteService {

    private final RestClient restClient;

    public NoteService(@Qualifier("gatewayRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public List<Note> getNotesByPatient(Long patId) {
        return Arrays.asList(
                Objects.requireNonNull(restClient.get()
                        .uri("/notes-service/notes/patient/{patId}", patId)
                        .retrieve()
                        .body(Note[].class))
        );
    }

    public void save(Note note) {
        restClient.post()
                .uri("/notes-service/notes")
                .body(note)
                .retrieve()
                .toBodilessEntity();
    }

    public void delete(String id) {
        restClient.delete()
                .uri("/notes-service/notes/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }
}