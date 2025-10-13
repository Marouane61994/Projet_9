package com.medilabo.front.Controller;



import com.medilabo.front.Service.PatientService;
import com.medilabo.front.model.Patient;



import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class PatientController {

    private final PatientService patientService;


    public PatientController(PatientService patientService) {
        this.patientService = patientService;

    }

    @GetMapping("/patients")
    public String listPatients(Model model) {
        List<Patient> patients = patientService.getAllPatients();
        model.addAttribute("patients", patients);
        return "patients";
    }

    @GetMapping("/patients/{id}")
    public String patientDetails(@PathVariable Long id, Model model) {
        Patient patient = patientService.getPatientById(id);


        model.addAttribute("patient", patient);
        return "patient-details";
    }
}
