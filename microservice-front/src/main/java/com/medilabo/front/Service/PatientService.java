package com.medilabo.front.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClient;


import com.medilabo.front.model.Patient;
import org.springframework.stereotype.Service;


import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@Service
public class PatientService {

    private final RestClient restClient;

    public PatientService(RestClient.Builder builder,
                          @Value("${gateway.url}") String gatewayUrl) {
        String basicAuth = Base64.getEncoder()
                .encodeToString("user:password".getBytes(StandardCharsets.UTF_8));

        this.restClient = builder
                .baseUrl(gatewayUrl + "/patient-service")
                .defaultHeader("Authorization", "Basic " + basicAuth)
                .build();
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

    public Patient createPatient(Patient patient) {
        return restClient.post()
                .uri("/patients")
                .body(patient)
                .retrieve()
                .body(Patient.class);
    }
}

