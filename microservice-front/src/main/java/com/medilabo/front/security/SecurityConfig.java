package com.medilabo.front.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de la sécurité de l'interface utilisateur (Frontend).
 * * ROLE :
 * Gérer l'authentification des utilisateurs finaux via un formulaire de connexion,
 * sécuriser l'accès aux pages HTML et gérer le cycle de vie des sessions (Logout).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${auth.front.username}")
    private String frontUsername;

    @Value("${auth.front.password}")
    private String frontPassword;

    /**
     * Utilisation de BCrypt pour le hachage des mots de passe utilisateurs.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Définition de l'utilisateur autorisé à accéder à l'application.
     * Dans un contexte de production, ces données pourraient être migrées vers une base SQL,
     * mais l'approche In-Memory est ici privilégiée pour la gestion d'un accès administrateur technique.
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails user = User.builder()
                .username(frontUsername)
                .password(encoder.encode(frontPassword))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    /**
     * Configuration de la chaîne de sécurité HTTP.
     * - Permet l'accès libre aux ressources statiques (CSS, JS) et à la page de login.
     * - Configure le formulaire de login personnalisé.
     * - Sécurise la déconnexion en invalidant la session et en supprimant les cookies.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Autorise l'accès aux ressources graphiques sans authentification
                        .requestMatchers("/css/**", "/images/**", "/js/**", "/webjars/**", "/login").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/patients", true) // Redirection après connexion réussie
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true) // Sécurité : tue la session côté serveur
                        .deleteCookies("JSESSIONID") // Sécurité : supprime le cookie côté client
                        .permitAll()
                );

        return http.build();
    }
}