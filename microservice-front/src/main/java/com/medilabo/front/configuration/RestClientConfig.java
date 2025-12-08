package com.medilabo.front.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
public class RestClientConfig {


    @Bean
    public RestClient restClient(RestClient.Builder builder,
                                 @Value("${gateway.url}") String gatewayUrl) {

        String username = "user";
        String password = "password";


        String auth = username + ":" + password;
        String basicAuth = Base64.getEncoder()
                .encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + basicAuth;

        return builder
                .baseUrl(gatewayUrl)
                .defaultHeader("Authorization", authHeader)
                .build();
    }
}