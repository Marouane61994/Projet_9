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

    public DiabetesService(@Value("${gateway.url}") String gatewayUrl) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = gatewayUrl + "/diabetes-service";
    }


    public Map getDiabetesReport(Long patientId) {
        LOGGER.info("Récupération du rapport de diabète");
        String url = baseUrl + "/assess/" + patientId;

        String username = "technical_user_gateway";
        String password = "password";

        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + encodedAuth;

        HttpHeaders headers = new HttpHeaders();
       headers.set("Authorization", authHeader);

        try {
            return restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Map.class
            ).getBody();
        } catch (Exception e) {
            LOGGER.error("Erreur pendant la récupération du rapport", e);
            return null;
        }
    }

}

