package com.medilabo.front.controller;

import com.medilabo.front.service.DiabetesService;
import com.medilabo.front.service.NoteService;
import com.medilabo.front.service.PatientService;
import com.medilabo.front.model.Note;
import com.medilabo.front.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/patients")
public class PatientController {

    private final NoteService noteService;
    private final PatientService patientService;

    @Autowired
    private DiabetesService diabetesService;

    public PatientController(NoteService noteService, PatientService patientService) {
        this.noteService = noteService;
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
    public String getPatientDetails(@PathVariable Long id, Model model) {

        Patient patient = patientService.getPatientById(id);
        List<Note> notes = noteService.getNotesByPatient(id);

        Map<String, Object> rapport = diabetesService.getDiabetesReport(id);

        model.addAttribute("patient", patient);
        model.addAttribute("notes", notes);

        if (rapport != null) {
            model.addAttribute("age", rapport.get("age"));
            model.addAttribute("triggerCount", rapport.get("nombreDeclencheurs"));
            model.addAttribute("riskLevel", rapport.get("niveauRisque"));
        } else {
            model.addAttribute("age", "N/A");
            model.addAttribute("triggerCount", "N/A");
            model.addAttribute("riskLevel", "Indisponible");
        }

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
