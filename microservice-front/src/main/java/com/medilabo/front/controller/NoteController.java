package com.medilabo.front.controller;

import com.medilabo.front.service.NoteService;
import com.medilabo.front.service.PatientService;
import com.medilabo.front.model.Note;
import com.medilabo.front.model.Patient;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@Data
@Controller
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;
    private final PatientService patientService;


    /**
     * Afficher les détails du patient et ses notes.
     */
    @GetMapping("/patient/{patId}")
    public String getNotesByPatient(@PathVariable Long patId, Model model) {
        Patient patient = patientService.getPatientById(patId);
        List<Note> notes = noteService.getNotesByPatient(patId);

        model.addAttribute("patient", patient);
        model.addAttribute("notes", notes);
        return "patient-details";
    }

    /**
     * Ajouter une nouvelle note pour un patient.
     */
    @PostMapping
    public String addNote(@ModelAttribute Note note) {
        noteService.save(note);
        return "redirect:/patients/" + note.getPatId();
    }


    @PostMapping("/delete/{id}")
    public String deleteNote(@PathVariable String id,
                             @RequestParam Long patId) {

        noteService.delete(id);

        return "redirect:/patients/" + patId;
    }
}
