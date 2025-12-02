package com.medilabo.front.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
        LOGGER.info("Recupération du rapport de diabetes");
        String url = baseUrl + "/assess/" + patientId;
        HttpHeaders headers =new HttpHeaders() ;
        headers.set("Authorization","Basic TODO");
        try {
            return (Map) restTemplate.<Map>exchange(url, HttpMethod.GET,new HttpEntity<>(headers), Map.class);
        } catch (Exception e) {
            LOGGER.error("Erreur pendant la récuperation du rapport ",e);
            return null;
        }
    }
}
