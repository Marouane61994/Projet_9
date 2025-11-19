package com.medilabo.front.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class DiabetesService {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public DiabetesService(@Value("${gateway.url}") String gatewayUrl) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = gatewayUrl + "/diabetes-service";
    }

    public Map getDiabetesReport(Long patientId) {
        String url = baseUrl + "/assess/" + patientId;

        System.out.println("URL utilisée par le front → " + url);

        try {
            return restTemplate.getForObject(url, Map.class);
        } catch (Exception e) {
            System.out.println("Erreur front : " + e.getMessage());
            return null;
        }
    }
}
