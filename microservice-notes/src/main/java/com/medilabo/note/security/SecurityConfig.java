package com.medilabo.note.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Utilise DelegatingPasswordEncoder qui est la méthode recommandée par Spring
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    /**
     * Définit les utilisateurs en mémoire pour l'authentification Basic.
     * Les identifiants "user:password" sont utilisés par le microservice Front-end.
     */
    @Bean
    public InMemoryUserDetailsManager userDetailsService(PasswordEncoder encoder) {
        UserDetails user = User.withUsername("user")
                .password(encoder.encode("password"))
                .roles("USER")
                .build();

        // Ajout d'un utilisateur admin pour plus de flexibilité (facultatif mais bonne pratique)
        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    /**
     * Configure les règles de sécurité.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Désactive le CSRF, standard pour les APIs stateless
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Assure que TOUTES les requêtes vers le microservice "note" nécessitent une authentification
                        .anyRequest().authenticated()
                )
                // Active l'authentification HTTP Basic
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}