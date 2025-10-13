package com.medilabo.front.Service;

import org.springframework.web.client.RestClient;


import com.medilabo.front.model.Patient;
import org.springframework.stereotype.Service;


import java.util.Arrays;
import java.util.List;

@Service
public class PatientService {

    private final RestClient restClient;

    public PatientService(RestClient.Builder builder) {
        this.restClient = builder.baseUrl("http://gateway:8080/patient-service").build();
    }

    public List<Patient> getAllPatients() {
        Patient[] patients = restClient.get()
                .uri("/patients")
                .retrieve()
                .body(Patient[].class);
        return Arrays.asList(patients);
    }

    public Patient getPatientById(Long id) {
        return restClient.get()
                .uri("/patients/{id}", id)
                .retrieve()
                .body(Patient.class);
    }
}

