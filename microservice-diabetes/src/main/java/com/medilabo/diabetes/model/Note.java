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
}
