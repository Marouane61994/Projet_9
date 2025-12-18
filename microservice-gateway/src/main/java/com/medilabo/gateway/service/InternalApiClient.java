package com.medilabo.gateway.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class InternalApiClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final Map<String, String> SERVICE_CREDENTIALS = new HashMap<>();

    @Value("${internal.base-url}")
    private String internalBaseUrl;

    public InternalApiClient(
            @Value("${auth.patient.username}:${auth.patient.password}") String patientCreds,
            @Value("${auth.note.username}:${auth.note.password}") String noteCreds,
            @Value("${auth.diabetes.username}:${auth.diabetes.password}") String diabetesCreds) {

        SERVICE_CREDENTIALS.put("/patient-service", patientCreds);
        SERVICE_CREDENTIALS.put("/note-service", noteCreds);
        SERVICE_CREDENTIALS.put("/diabetes-service", diabetesCreds);
    }

    public Object sendRequest(String servicePath, String uri, HttpMethod method, Object body, Class<?> responseType) {

        String credentials = SERVICE_CREDENTIALS.get(servicePath);
        if (credentials == null) {
            throw new IllegalArgumentException("Service path non reconnu: " + servicePath);
        }

        String basicAuth = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth);

        String fullUrl = internalBaseUrl + servicePath + uri;

        return restTemplate.exchange(
                fullUrl,
                method,
                new HttpEntity<>(body, headers),
                responseType
        ).getBody();
    }
}