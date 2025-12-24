package com.medilabo.diabetes.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${patients.service.url}")
    private String patientServiceUrl;

    @Value("${notes.service.url}")
    private String noteServiceUrl;

    @Value("${auth.patient.username}")
    private String patientUsername;

    @Value("${auth.patient.password}")
    private String patientPassword;

    @Value("${auth.note.username}")
    private String noteUsername;

    @Value("${auth.note.password}")
    private String notePassword;

    @Bean
    public RestClient patientRestClient() {
        return RestClient.builder()
                .baseUrl(patientServiceUrl)
                .defaultHeaders(headers -> headers.setBasicAuth(patientUsername, patientPassword))
                .build();
    }

    @Bean
    public RestClient noteRestClient() {
        return RestClient.builder()
                .baseUrl(noteServiceUrl)
                .defaultHeaders(headers -> headers.setBasicAuth(noteUsername, notePassword))
                .build();
    }
}