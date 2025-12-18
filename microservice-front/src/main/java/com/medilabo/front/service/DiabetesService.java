package com.medilabo.front.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
public class DiabetesService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiabetesService.class);
    private final RestTemplate restTemplate;
    private final String baseUrl;

    private final String gatewayUsername;
    private final String gatewayPassword;

    public DiabetesService(
            @Value("${gateway.url}") String gatewayUrl,
            @Value("${auth.gateway.username}") String gatewayUsername,
            @Value("${auth.gateway.password}") String gatewayPassword) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = gatewayUrl + "/diabetes-service";
        this.gatewayUsername = gatewayUsername;
        this.gatewayPassword = gatewayPassword;
    }

    public Map getDiabetesReport(Long patientId) {
        LOGGER.info("Récupération du rapport de diabète pour le patient ID: {}", patientId);
        String url = baseUrl + "/assess/" + patientId;

        String auth = gatewayUsername + ":" + gatewayPassword;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Basic " + encodedAuth);

        try {
            return restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Map.class
            ).getBody();
        } catch (Exception e) {
            LOGGER.error("Erreur pendant la récupération du rapport pour le patient {}", patientId, e);
            return Map.of("error", "Service indisponible");
        }
    }
}