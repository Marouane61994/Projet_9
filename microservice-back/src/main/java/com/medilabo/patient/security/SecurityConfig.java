package com.medilabo.patient.security;

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
 * Configuration de la sécurité interne du microservice Patient.
 * * ROLE :
 * Ce service est protégé par une authentification Basic. Il n'accepte que les
 * requêtes provenant de la Gateway (ou d'un administrateur autorisé) possédant
 * les identifiants techniques définis dans le fichier .env.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${auth.patients.username}")
    private String patientUsername;

    @Value("${auth.patients.password}")
    private String patientPassword;

    /**
     * Bean de hachage des mots de passe utilisant BCrypt.
     * Utilisé pour valider le mot de passe reçu dans le header Authorization.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Définition de l'utilisateur technique autorisé à interroger ce service.
     * Les informations sont stockées en mémoire (In-Memory) car il s'agit
     * d'identifiants de service à service et non d'utilisateurs finaux.
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails gatewayUser = User.builder()
                .username(patientUsername)
                .password(encoder.encode(patientPassword))
                .roles("Patients")
                .build();

        return new InMemoryUserDetailsManager(gatewayUser);
    }

    /**
     * Chaîne de filtres de sécurité pour Spring MVC.
     * - CSRF : Désactivé car le service est une API REST sans état.
     * - Authorization : Toutes les requêtes doivent être authentifiées.
     * - HttpBasic : Utilisation du standard Basic Auth (Base64) pour la réception des credentials.
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