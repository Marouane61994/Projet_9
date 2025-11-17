package com.medilabo.front.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Controller
public class DiabetesController {

    private final RestClient restClient;

    public DiabetesController(RestClient.Builder builder,
                              @Value("${gateway.url}") String gatewayUrl) {
        this.restClient = builder
                .baseUrl(gatewayUrl + "/diabetes-service")
                .build();
    }

    @GetMapping("/patients/{id}/assess")
    public String getDiabetesReport(@PathVariable Long id, Model model) {
        Map report = restClient.get()
                .uri("/assess/{id}", id)
                .retrieve()
                .body(Map.class);

        model.addAttribute("report", report);
        return "diabetes-report";
    }
}
