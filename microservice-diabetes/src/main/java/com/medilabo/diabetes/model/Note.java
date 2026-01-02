package com.medilabo.diabetes.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Note {
    @Id
    private String id;
    private Long patId;
    private String patient;
    private String note;
    private String normalizedNote; // Note nettoyée pour le calcul

    /**
     * Normalise le texte de la note pour faciliter la détection des triggers.
     * Conversion en minuscules et suppression des caractères spéciaux si nécessaire.
     */
    private String normalize(String input) {
        if (input == null) return "";
        return input.toLowerCase().trim();
    }
}
