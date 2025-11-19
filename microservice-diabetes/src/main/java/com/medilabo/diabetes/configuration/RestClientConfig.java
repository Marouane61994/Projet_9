package com.medilabo.diabetes.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient(RestClient.Builder builder,
                                 @Value("${gateway.url}") String gatewayUrl) {
        return builder
                .baseUrl(gatewayUrl)
                .build();
    }
}
