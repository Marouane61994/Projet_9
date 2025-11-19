package com.medilabo.diabetes.repository;

import com.medilabo.diabetes.model.Patient;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

@Repository
public class PatientRepository {

    private final RestClient restClient;

    public PatientRepository(RestClient restClient) {
        this.restClient = restClient;
    }


    public Patient getPatientById(Long patId) {
        return restClient.get()
                .uri("/patient-service/patients/{id}", patId)
                .retrieve()
                .body(Patient.class);
    }
}
