package com.medilabo.front.model;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "notes")
@Data
public class Note {
    @Id
    private String id;
    private Long patId;
    private String patient;
    private String note;
}
