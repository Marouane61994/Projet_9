package com.medilabo.note.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.util.Base64;

/**
 * Configuration du client HTTP pour la communication avec le microservice Patient.
 * <p>
 * Cette classe définit un Bean {@link RestClient} configuré avec :
 * <ul>
 * <li>L'URL de base du service Patient via {@code patients.service.url}</li>
 * <li>Une authentification Basic HTTP sécurisée pour les échanges service-à-service</li>
 * </ul>
 * </p>
 */
@Configuration
public class RestClientConfig {

    @Value("${patients.service.url}")
    private String patientServiceUrl;

    @Value("${auth.patients.username}")
    private String username;

    @Value("${auth.patients.password}")
    private String password;

    @Bean
    public RestClient patientRestClient() {
        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        return RestClient.builder()
                .baseUrl(patientServiceUrl)
                .defaultHeader("Authorization", "Basic " + encodedAuth)
                .build();
    }
}