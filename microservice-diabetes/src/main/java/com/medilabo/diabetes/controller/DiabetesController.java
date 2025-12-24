package com.medilabo.diabetes.controller;

import com.medilabo.diabetes.model.Note;
import com.medilabo.diabetes.model.Patient;
import com.medilabo.diabetes.service.DiabetesService;
import com.medilabo.diabetes.service.NoteService;
import com.medilabo.diabetes.service.PatientService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
public class DiabetesController {

    private final DiabetesService diabetesService;
    private final PatientService patientService;
    private final NoteService noteService;

    public DiabetesController(DiabetesService diabetesService,
                              PatientService patientService,
                              NoteService noteService) {
        this.diabetesService = diabetesService;
        this.patientService = patientService;
        this.noteService = noteService;
    }

    @GetMapping("/assessment/{id}")
    public Map<String, Object> getAssessment(@PathVariable Long id) {

        Patient patient = patientService.getPatientById(id);
        List<Note> notes = noteService.getNotesByPatient(id);

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