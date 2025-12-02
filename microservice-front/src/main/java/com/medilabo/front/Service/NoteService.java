package com.medilabo.front.Service;

import com.medilabo.front.model.Note;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Service
public class NoteService {

    private final RestClient restClient;

    public NoteService(RestClient.Builder builder,
                       @Value("${gateway.url}") String gatewayUrl) {

        String basicAuth = Base64.getEncoder()
                .encodeToString("user:password".getBytes(StandardCharsets.UTF_8));

        this.restClient = builder
                .baseUrl(gatewayUrl + "/note-service")
                .defaultHeader("Authorization", "Basic " + basicAuth)
                .build();
    }

    public List<Note> getNotesByPatient(Long patId) {
        return Arrays.asList(
                Objects.requireNonNull(restClient.get()
                        .uri("/notes/patient/{patId}", patId)
                        .retrieve()
                        .body(Note[].class))
        );
    }
    public void save(Note note) {
        restClient.post()
                .uri("/notes")  // endpoint du microservice pour créer une note
                .body(note)
                .retrieve()
                .body(Note.class);
    }

    public void delete(String id) {
        restClient.delete()
                .uri("/notes/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }



}