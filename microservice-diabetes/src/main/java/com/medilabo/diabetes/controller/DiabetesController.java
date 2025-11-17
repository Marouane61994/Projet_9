package com.medilabo.diabetes.controller;

import com.medilabo.diabetes.service.DiabetesService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/diabetes")
public class DiabetesController {

    private final DiabetesService diabetesService;

    public DiabetesController(DiabetesService diabetesService) {
        this.diabetesService = diabetesService;
    }

    @GetMapping("/patients/{id}/assess")
    public String getDiabetesReport(@PathVariable Long id, Model model) {
        String report = diabetesService.assessDiabetesRisk(id);

        model.addAttribute("report", report);
        return "diabetes-report";
    }

}
