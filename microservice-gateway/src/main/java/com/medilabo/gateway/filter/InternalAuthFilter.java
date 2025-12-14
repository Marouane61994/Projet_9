package com.medilabo.gateway.filter;// Ceci est une version simplifiée. Les filtres sont complexes à implémenter correctement.

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
import java.util.Map;

@Component
public class InternalAuthFilter implements GlobalFilter, Ordered {

    private static final Map<String, String> SERVICE_CREDENTIALS = Map.of(
            "/patient-service", "technical_user_patient:password",
            "/note-service", "technical_user_notes:password",
            "/diabetes-service", "technical_user_diabetes:password"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // Trouver le chemin de base du service
        String servicePath = SERVICE_CREDENTIALS.keySet().stream()
                .filter(path::startsWith)
                .findFirst()
                .orElse(null);

        if (servicePath != null) {
            String credentials = SERVICE_CREDENTIALS.get(servicePath);
            String authHeader = getBasicAuthHeader(credentials);

            // 1. Supprimer l'ancien en-tête (celui du Front-end)
            ServerHttpRequest.Builder builder = request.mutate();
            builder.headers(httpHeaders -> httpHeaders.remove(HttpHeaders.AUTHORIZATION));

            // 2. Ajouter le nouvel en-tête pour le service interne
            builder.header(HttpHeaders.AUTHORIZATION, authHeader);

            return chain.filter(exchange.mutate().request(builder.build()).build());
        }

        // Si non trouvé, continuer la chaîne de filtres (ex: appel public)
        return chain.filter(exchange);
    }

    private String getBasicAuthHeader(String credentials) {
        String basicAuth = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return "Basic " + basicAuth;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE; // Exécuter ce filtre très tôt
    }
}