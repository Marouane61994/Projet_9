package com.medilabo.diabetes.controller;

import com.medilabo.diabetes.model.Note;
import com.medilabo.diabetes.model.Patient;
import com.medilabo.diabetes.repository.NoteRepository;
import com.medilabo.diabetes.repository.PatientRepository;
import com.medilabo.diabetes.service.DiabetesService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("")
public class DiabetesController {

    private final DiabetesService diabetesService;
    private final PatientRepository patientRepository;
    private final NoteRepository noteRepository;

    public DiabetesController(DiabetesService diabetesService, PatientRepository patientRepository, NoteRepository noteRepository) {
        this.diabetesService = diabetesService;
        this.patientRepository = patientRepository;
        this.noteRepository = noteRepository;
    }

    @GetMapping("/diabetes/{id}")
    public Map<String, Object> getAssessment(@PathVariable Long id) {
        Patient patient = patientRepository.getPatientById(id);
        List<Note> notes = noteRepository.getNotesByPatient(id);

        int nombreDeclencheurs = diabetesService.countTriggers(notes);
        int age = diabetesService.calculateAge(LocalDate.parse(patient.getDateNaissance()));
        String niveauRisque = diabetesService.determineRiskLevel(patient.getGenre(), age, nombreDeclencheurs);

        return Map.of(
                "patientId", id,
                "prenom", patient.getPrenom(),
                "nom", patient.getNom(),
                "genre", patient.getGenre(),
                "dateNaissance", patient.getDateNaissance(),
                "age", age,
                "nombreDeclencheurs", nombreDeclencheurs,
                "niveauRisque", niveauRisque
        );
    }
}
