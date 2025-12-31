package com.medilabo.front.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Note {
    private String id;
    private Long patId;
    private String patient;
    private String note;
}
