package com.medilabo.diabetes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients("com.medilabo.diabetes.proxy")
public class MicroserviceDiabetesApplication {
    public static void main(String[] args) {
        SpringApplication.run(MicroserviceDiabetesApplication.class, args);
    }
}
