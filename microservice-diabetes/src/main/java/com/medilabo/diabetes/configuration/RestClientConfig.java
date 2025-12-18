package com.medilabo.diabetes.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class RestClientConfig {

    @Value("${gateway.url}")
    private String gatewayUrl;

    @Value("${auth.gateway.username}")
    private String gatewayUsername;

    @Value("${auth.gateway.password}")
    private String gatewayPassword;

    @Bean
    public RestClient restClient() {

        String authString = gatewayUsername + ":" + gatewayPassword;

        String basicAuth = Base64.getEncoder()
                .encodeToString(authString.getBytes(StandardCharsets.UTF_8));

        String authHeader = "Basic " + basicAuth;

        return RestClient.builder()
                .baseUrl(gatewayUrl)
                .defaultHeader("Authorization", authHeader)
                .build();
    }
}