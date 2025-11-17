package com.medilabo.diabetes.model;

import lombok.Data;

@Data
public class Patient {
    private Long id;
    private String nom;
    private String prenom;
    private String dateNaissance;
    private String genre;
    private String adresse;
    private String telephone;
}
