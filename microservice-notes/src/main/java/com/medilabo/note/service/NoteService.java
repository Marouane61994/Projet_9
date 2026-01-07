package com.medilabo.note.service;

import com.medilabo.note.model.Note;
import com.medilabo.note.repository.NoteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class NoteService {

    private final NoteRepository repository;
    private final RestClient patientRestClient; // Le client pour parler à l'autre service

    public List<Note> getAllNotes() {
        return repository.findAll();
    }

    public NoteService(NoteRepository repository, RestClient patientRestClient) {
        this.repository = repository;
        this.patientRestClient = patientRestClient;
    }
    public List<Note> getNotesByPatient(Long patId) {
        return repository.findByPatId(patId);
    }

    /**
     * Enregistre une nouvelle note de consultation après avoir validé l'existence du patient.
     * <p>
     * Cette méthode effectue un appel synchrone vers le microservice <b>Patient</b>
     * pour garantir l'intégrité référentielle entre la base MongoDB (Notes) et
     * la base MySQL (Patients).
     * </p>
     *
     * @param note L'objet {@link Note} contenant l'ID du patient et le contenu de la note.
     * @return La note enregistrée en base de données avec son identifiant technique MongoDB.
     * @throws ResponseStatusException Si le patient n'existe pas ou si le service
     * Patient est injoignable (HTTP 404).
     */
    public Note save(Note note) {
        try {

            patientRestClient.get()
                    .uri("/patients/{id}", note.getPatId())
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new RuntimeException();
                    })
                    .toBodilessEntity();


            return repository.save(note);

        } catch (Exception e) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Le patient avec l'ID " + note.getPatId() + " n'existe pas"
            );
        }
    }

    public Note update(String id, Note note) {
        note.setId(id);
        return repository.save(note);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
}


