package com.medilabo.diabetes.service;

import com.medilabo.diabetes.model.Note;
import com.medilabo.diabetes.model.Patient;
import com.medilabo.diabetes.repository.NoteRepository;
import com.medilabo.diabetes.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Locale;

@Service
public class DiabetesService {

    private final PatientRepository patientRepository;
    private final NoteRepository noteRepository;

    public DiabetesService(PatientRepository patientRepository,
                           NoteRepository noteRepository) {
        this.patientRepository = patientRepository;
        this.noteRepository = noteRepository;
    }

    public String assessDiabetesRisk(Long patId) {

        Patient patient = patientRepository.getPatientById(patId);
        List<Note> notes = noteRepository.getNotesByPatient(patId);

        int triggerCount = countTriggers(notes);
        int age = calculateAge(LocalDate.parse(patient.getDateNaissance()));
        String sex = patient.getGenre();

        return determineRiskLevel(sex, age, triggerCount);
    }

    public int calculateAge(LocalDate birthDate) {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public int countTriggers(List<Note> notes) {
        String[] triggers = {
                "hémoglobine a1c", "microalbumine", "taille", "poids",
                "fumeur", "fumeuse", "anormal", "cholestérol",
                "vertiges", "rechute", "réaction", "anticorps"
        };

        int count = 0;

        for (String trigger : triggers) {
            for (Note note : notes) {
                if (note.getNote() != null) {

                    String cleanNote = Normalizer.normalize(note.getNote(), Normalizer.Form.NFD)
                            .replaceAll("\\p{M}", "")
                            .toLowerCase(Locale.ROOT);

                    String cleanTrigger = Normalizer.normalize(trigger, Normalizer.Form.NFD)
                            .replaceAll("\\p{M}", "")
                            .toLowerCase(Locale.ROOT);

                    if (cleanNote.contains(cleanTrigger)) {
                        count++;
                        break;
                    }
                }
            }
        }
        return count;
    }

    public String determineRiskLevel(String sex, int age, int triggerCount) {
        if (triggerCount == 0) return "None";

        if (age > 30) {
            if (triggerCount >= 8) return "Early onset";
            if (triggerCount >= 6) return "In Danger";
            if (triggerCount >= 2) return "Borderline";
        } else {
            if ("M".equalsIgnoreCase(sex)) {
                if (triggerCount >= 5) return "Early onset";
                if (triggerCount >= 3) return "In Danger";
            } else if ("F".equalsIgnoreCase(sex)) {
                if (triggerCount >= 7) return "Early onset";
                if (triggerCount >= 4) return "In Danger";
            }
        }

        return "None";
    }
}
