package com.medilabo.front.Controller;

import com.medilabo.front.Service.PatientService;
import com.medilabo.front.model.Patient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public String listPatients(Model model,
                               @RequestParam(value = "success", required = false) String successMessage) {
        List<Patient> patients = patientService.getAllPatients();
        model.addAttribute("patients", patients);
        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
        }
        return "patients";
    }

    @GetMapping("/{id}")
    public String patientDetails(@PathVariable Long id, Model model) {
        Patient patient = patientService.getPatientById(id);
        model.addAttribute("patient", patient);
        return "patient-details";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Patient patient = patientService.getPatientById(id);
        model.addAttribute("patient", patient);
        return "patient-edit";
    }

    @PostMapping("/{id}/edit")
    public String updatePatient(@PathVariable Long id,
                                @ModelAttribute Patient patient,
                                RedirectAttributes redirectAttributes) {
        patient.setId(id);
        patientService.updatePatient(id, patient);

        redirectAttributes.addAttribute("success", "Le patient a bien été modifié !");
        return "redirect:/patients";
    }
}
