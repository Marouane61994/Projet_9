package com.medilabo.front.service;

import com.medilabo.front.model.Patient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Service
public class PatientService {

    private final RestClient restClient;

    public PatientService(RestClient.Builder builder,
                          @Value("${gateway.url}") String gatewayUrl,
                          @Value("${auth.gateway.username}") String gatewayUsername,
                          @Value("${auth.gateway.password}") String gatewayPassword) {

        String authString = gatewayUsername + ":" + gatewayPassword;
        String basicAuth = Base64.getEncoder()
                .encodeToString(authString.getBytes(StandardCharsets.UTF_8));

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

        return Arrays.asList(Objects.requireNonNull(patients));
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