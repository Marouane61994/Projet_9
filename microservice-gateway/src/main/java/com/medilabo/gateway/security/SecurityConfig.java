package com.medilabo.gateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuration de la sécurité périmétrique de la Gateway.
 * * Cette classe définit comment la Gateway authentifie les requêtes entrantes (venant du Front)
 * avant de les laisser passer vers les microservices internes.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${auth.gateway.username}")
    private String gatewayUsername;

    @Value("${auth.gateway.password}")
    private String gatewayPassword;

    /**
     * Définit l'algorithme de hachage des mots de passe.
     * BCrypt est utilisé ici car il intègre un sel (salt) aléatoire par défaut,
     * offrant une protection robuste contre les attaques par dictionnaire.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuration des utilisateurs autorisés à traverser la Gateway.
     * Les identifiants sont récupérés depuis les variables d'environnement (.env).
     */
    @Bean
    public MapReactiveUserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails user = User.builder()
                .username(gatewayUsername)
                .password(encoder.encode(gatewayPassword))
                .roles("GATEWAY")
                .build();
        return new MapReactiveUserDetailsService(user);
    }

    /**
     * Définition de la chaîne de filtres de sécurité.
     * - CSRF désactivé : les microservices étant sans état (stateless) et protégés par auth,
     * le jeton CSRF n'est pas nécessaire et simplifie les appels API.
     * - Authentification Basic : Recommandée pour les échanges techniques entre services.
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .build();
    }
}