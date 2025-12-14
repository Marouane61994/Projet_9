package com.medilabo.gateway.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate; // Utilisé car plus simple pour cette logique

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
public class InternalApiClient {

    private final RestTemplate restTemplate = new RestTemplate();

    // Map des identifiants techniques des services internes (Doit être sécurisé, idéalement injecté)
    private static final Map<String, String> SERVICE_CREDENTIALS = Map.of(
            "/patient-service", "technical_user_patient:password",
            "/note-service", "technical_user_notes:password",
            "/diabetes-service", "technical_user_diabetes:password"
    );

    @Value("${internal.base-url}") // Ajoutez cette propriété dans application.yml (ex: http://localhost)
    private String internalBaseUrl;

    public Object sendRequest(String servicePath, String uri, HttpMethod method, Object body, Class<?> responseType) {

        // 1. Déterminer l'identifiant pour le service cible
        String credentials = SERVICE_CREDENTIALS.get(servicePath);
        if (credentials == null) {
            throw new IllegalArgumentException("Service path non reconnu pour l'authentification interne: " + servicePath);
        }

        // 2. Créer l'en-tête Basic Auth
        String basicAuth = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + basicAuth);

        String fullUrl = internalBaseUrl + servicePath + uri;

        // 3. Exécuter la requête
        return restTemplate.exchange(
                fullUrl,
                method,
                new HttpEntity<>(body, headers),
                responseType
        ).getBody();
    }
}