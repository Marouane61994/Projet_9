package com.medilabo.note.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de la sécurité pour le microservice Note.
 * * ROLE :
 * Ce service gère les accès aux notes cliniques stockées dans MongoDB.
 * Il n'accepte que les requêtes authentifiées provenant de la Gateway,
 * garantissant que les données sensibles des patients ne sont pas exposées
 * directement sur le réseau.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${auth.notes.username}")
    private String noteUsername;

    @Value("${auth.notes.password}")
    private String notePassword;

    /**
     * Bean de hachage BCrypt.
     * Utilisé pour comparer de manière sécurisée les identifiants techniques
     * reçus via la Gateway avec ceux stockés en mémoire.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuration de l'utilisateur technique autorisé (Service-to-Service).
     * Le rôle "SERVICE_API" identifie les appels provenant de l'infrastructure interne.
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails gatewayUser = User.builder()
                .username(noteUsername)
                .password(encoder.encode(notePassword))
                .roles("SERVICE_API")
                .build();

        return new InMemoryUserDetailsManager(gatewayUser);
    }

    /**
     * Chaîne de filtres de sécurité :
     * - CSRF : Désactivé car l'API est stateless (sans session).
     * - Authorization : Authentification obligatoire pour tous les endpoints (/notes/**).
     * - HttpBasic : Réception des credentials encodés en Base64 par la Gateway.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}