package com.medilabo.front.Controller;


import com.medilabo.front.Service.DiabetesService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class DiabetesController {

    private final DiabetesService diabetesService;

    public DiabetesController(DiabetesService diabetesService) {
        this.diabetesService = diabetesService;
    }

    @GetMapping("/patients/{id}/assess")
    public String getDiabetesReport(@PathVariable Long id, Model model) {
        Map<String, Object> report = diabetesService.getDiabetesReport(id);

        if (report == null) {
            model.addAttribute("error", "Impossible de récupérer le rapport de diabète pour ce patient.");
            return "diabetes-report";
        }

        model.addAttribute("report", report);
        model.addAttribute("patientId", id);
        return "diabetes-report";
    }
}
