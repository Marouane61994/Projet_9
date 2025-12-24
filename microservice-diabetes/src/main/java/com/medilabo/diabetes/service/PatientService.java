package com.medilabo.diabetes.service;

import com.medilabo.diabetes.model.Patient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Service
public class PatientService {

    private final RestClient restClient;

    public PatientService(@Qualifier("patientRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public List<Patient> getAllPatients() {
        Patient[] patients = restClient.get()
                .uri("/patients")
                .retrieve()
                .body(Patient[].class);

        return patients != null ? Arrays.asList(patients) : List.of();
    }

    public Patient getPatientById(Long id) {
        return restClient.get()
                .uri("/patients/{id}", id)
                .retrieve()
                .body(Patient.class);
    }

    public void updatePatient(Long id, Patient patient) {
        restClient.put()
                .uri("/patients/{id}", id)
                .body(patient)
                .retrieve()
                .toBodilessEntity();
    }
}