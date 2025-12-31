package com.medilabo.front.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.Map;

@Service
public class DiabetesService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiabetesService.class);
    private final RestClient restClient;

    public DiabetesService(@Qualifier("gatewayRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public Map getDiabetesReport(Long patientId) {

        String uri = "/diabetes-service/assessment/" + patientId;

        LOGGER.info("Appel Gateway via RestClient à l'URI : {}", uri);

        try {
            return restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(Map.class);
        } catch (Exception e) {
            LOGGER.error("Échec de l'appel pour le patient {}: {}", patientId, e.getMessage());
            return Map.of("niveauRisque", "Indisponible (Erreur technique)");
        }
    }
}