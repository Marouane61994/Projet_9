package com.medilabo.diabetes.model;



import jakarta.persistence.Id;
import lombok.Data;


@Data
public class Note {
    @Id
    private String id;
    private Long patId;
    private String patient;
    private String note;
}
