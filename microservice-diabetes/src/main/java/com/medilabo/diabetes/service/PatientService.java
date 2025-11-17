package com.medilabo.diabetes.service;

import com.medilabo.diabetes.model.Patient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;



import org.springframework.stereotype.Service;


import java.util.Arrays;
import java.util.List;

@Service
public class PatientService {

    private final RestClient restClient;

    public PatientService(RestClient.Builder builder,
                          @Value("${gateway.url}") String gatewayUrl) {
        this.restClient = builder.baseUrl(gatewayUrl + "/patient-service").build();
    }

    public List<Patient> getAllPatients() {
        Patient[] patients = restClient.get()
                .uri("/patients")
                .retrieve()
                .body(Patient[].class);
        assert patients != null;
        return Arrays.asList(patients);
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
