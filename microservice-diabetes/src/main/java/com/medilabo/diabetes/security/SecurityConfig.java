package com.medilabo.diabetes.security;

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
 * Configuration de la sécurité pour le microservice Diabetes.
 * * ROLE :
 * Ce service assure que seuls les appels authentifiés (provenant principalement
 * de la Gateway via InternalAuthFilter) peuvent accéder aux calculs de risque.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${auth.diabetes.username}")
    private String diabetesUsername;

    @Value("${auth.diabetes.password}")
    private String diabetesPassword;

    /**
     * Définit l'encodeur BCrypt pour sécuriser le stockage temporaire
     * des identifiants techniques en mémoire.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configuration de l'utilisateur technique autorisé.
     * Le rôle "Diabetes" permet une granularité dans les droits d'accès.
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails gatewayUser = User.builder()
                .username(diabetesUsername)
                .password(encoder.encode(diabetesPassword))
                .roles("Diabetes")
                .build();

        return new InMemoryUserDetailsManager(gatewayUser);
    }

    /**
     * Configuration de la chaîne de filtres (Security Filter Chain) :
     * - Désactivation du CSRF : Adaptation au modèle Stateless des microservices API.
     * - Authentification HTTP Basic : Utilise l'encodage Base64 pour la transmission
     * des identifiants techniques entre la Gateway et ce service.
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