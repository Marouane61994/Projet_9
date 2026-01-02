package com.medilabo.gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Filtre de sécurité global pour la Gateway Medilabo.
 * * ROLE :
 * Ce filtre intercepte les requêtes entrantes et injecte dynamiquement un header
 * d'authentification Basic (Base64) avant de transmettre la requête aux microservices.
 * * POURQUOI CETTE MÉTHODE ?
 * 1. Sécurité "Service-to-Service" : Empêche l'accès direct aux microservices sans passer par la Gateway.
 * 2. Encodage Base64 : Standard HTTP pour l'authentification Basic, permettant de transmettre
 * des identifiants techniques (username:password) de manière structurée dans les headers.
 * 3. Centralisation : Le Front n'a pas besoin de connaître les secrets de chaque microservice.
 */
@Component
public class InternalAuthFilter implements GlobalFilter, Ordered {

    private final Map<String, String> SERVICE_CREDENTIALS = new HashMap<>();

    public InternalAuthFilter(
            @Value("${auth.patients.username}:${auth.patients.password}") String patientCreds,
            @Value("${auth.notes.username}:${auth.notes.password}") String noteCreds,
            @Value("${auth.diabetes.username}:${auth.diabetes.password}") String diabetesCreds)
    {
        SERVICE_CREDENTIALS.put("/patients-service", patientCreds);
        SERVICE_CREDENTIALS.put("/notes-service", noteCreds);
        SERVICE_CREDENTIALS.put("/diabetes-service", diabetesCreds);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Identification du service cible basé sur le chemin d'URL
        String servicePath = SERVICE_CREDENTIALS.keySet().stream()
                .filter(path::startsWith)
                .findFirst()
                .orElse(null);

        if (servicePath != null) {
            String credentials = SERVICE_CREDENTIALS.get(servicePath);

            // Génération du header "Basic Auth" (Encodage Base64 du couple user:password)
            // Cela transforme "user:pass" en une chaîne sécurisée pour le transport HTTP
            String authHeader = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

            // Mutation de la requête pour ajouter le header d'autorisation
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .header(HttpHeaders.AUTHORIZATION, authHeader)
                    .build();

            return chain.filter(exchange.mutate().request(request).build());
        }

        return chain.filter(exchange);
    }

    /**
     * Définit la priorité du filtre.
     * HIGHEST_PRECEDENCE assure que l'authentification est ajoutée avant tout autre traitement.
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}