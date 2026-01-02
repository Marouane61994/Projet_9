package com.medilabo.diabetes.service;

import com.medilabo.diabetes.model.Note;
import com.medilabo.diabetes.model.Patient;
import com.medilabo.diabetes.repository.NoteRepository;
import com.medilabo.diabetes.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Service responsable de l'évaluation du risque de diabète.
 * Le calcul repose sur le croisement des informations physiologiques du patient
 * (âge, sexe) et des déclencheurs (triggers) identifiés dans les notes cliniques.
 */
@Service
public class DiabetesService {

    private final PatientRepository patientRepository;
    private final NoteRepository noteRepository;

    // Liste des déclencheurs normalisés pour optimiser les performances (Green Code)
    private final List<String> normalizedTriggers;

    public DiabetesService(PatientRepository patientRepository,
                           NoteRepository noteRepository) {
        this.patientRepository = patientRepository;
        this.noteRepository = noteRepository;

        // Normalisation unique des triggers au démarrage du service
        // Cela évite de répéter l'opération coûteuse de Normalizer à chaque requête
        List<String> rawTriggers = Arrays.asList(
                "hémoglobine a1c", "microalbumine", "taille", "poids",
                "fumeur", "fumeuse", "anormal", "cholestérol",
                "vertige", "rechute", "réaction", "anticorps"
        );

        this.normalizedTriggers = rawTriggers.stream()
                .map(this::cleanString)
                .collect(Collectors.toList());
    }

    /**
     * Méthode principale évaluant le niveau de risque pour un patient donné.
     * @param patId Identifiant unique du patient
     * @return Libellé du niveau de risque (None, Borderline, In Danger, Early onset)
     */
    public String assessDiabetesRisk(Long patId) {
        Patient patient = patientRepository.getPatientById(patId);
        List<Note> notes = noteRepository.getNotesByPatient(patId);

        if (patient == null) return "Patient not found";

        int triggerCount = countTriggers(notes);
        int age = calculateAge(LocalDate.parse(patient.getDateNaissance()));
        String sex = patient.getGenre();

        return determineRiskLevel(sex, age, triggerCount);
    }

    /**
     * Calcule l'âge à partir de la date de naissance.
     */
    public int calculateAge(LocalDate birthDate) {
        if (birthDate == null) return 0;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Compte le nombre de triggers uniques présents dans l'ensemble des notes.
     * @param notes Liste des notes du patient récupérées depuis MongoDB
     * @return Nombre de mots-clés distincts trouvés
     */
    public int countTriggers(List<Note> notes) {
        if (notes == null || notes.isEmpty()) return 0;

        // On concatène et normalise toutes les notes pour une recherche unique
        String combinedNotes = notes.stream()
                .map(note -> note.getNote() != null ? note.getNote() : "")
                .map(this::cleanString)
                .collect(Collectors.joining(" "));

        int count = 0;
        for (String trigger : normalizedTriggers) {
            if (combinedNotes.contains(trigger)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Algorithme de décision basé sur les règles métier (âge, sexe, nombre de triggers).
     */
    public String determineRiskLevel(String sex, int age, int triggerCount) {
        if (triggerCount == 0) return "None";

        String firstLetterSex = (sex != null && !sex.isEmpty())
                ? sex.toUpperCase(Locale.ROOT).substring(0, 1)
                : "";

        // Cas des patients de plus de 30 ans
        if (age > 30) {
            if (triggerCount >= 8) return "Early onset";
            if (triggerCount >= 6) return "In Danger";
            if (triggerCount >= 2) return "Borderline";
            return "None";
        }

        // Cas des patients de 30 ans ou moins
        // Hommes (M ou H)
        if ("M".equals(firstLetterSex) || "H".equals(firstLetterSex)) {
            if (triggerCount >= 5) return "Early onset";
            if (triggerCount >= 3) return "In Danger";
        }
        // Femmes (F)
        else if ("F".equals(firstLetterSex)) {
            if (triggerCount >= 7) return "Early onset";
            if (triggerCount >= 4) return "In Danger";
        }

        return "None";
    }

    /**
     * Utilitaire de normalisation : supprime les accents et passe en minuscules.
     * Exemple : "Hémoglobine" -> "hemoglobine"
     */
    private String cleanString(String input) {
        if (input == null) return "";
        return Normalizer.normalize(input, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }
}