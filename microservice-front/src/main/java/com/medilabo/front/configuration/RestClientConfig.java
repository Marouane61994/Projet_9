package com.medilabo.front.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class RestClientConfig {

    /**
     * Crée un RestClient configuré pour appeler la Gateway avec l'authentification Basic Auth.
     */
    @Bean
    public RestClient restClient(@Value("${gateway.url}") String gatewayUrl) {

        // 🔑 1. Définition des identifiants techniques (ceux de la Gateway)
        String username = "technical_user_gateway";
        String password = "password";

        // 2. Encodage de l'authentification Basic Auth (username:password) en Base64
        String auth = username + ":" + password;
        String basicAuth = Base64.getEncoder()
                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + basicAuth;

        // 3. Configuration du RestClient avec le bon en-tête et l'URL de base
        return RestClient.builder()
                .baseUrl(gatewayUrl) // http://localhost:8083 (ou l'URL de votre Gateway)
                .defaultHeader("Authorization", authHeader) // Ajout de l'en-tête
                .build();
    }
}