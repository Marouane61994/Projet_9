package com.medilabo.note.controller;

import com.medilabo.note.model.Note;
import com.medilabo.note.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/notes")
public class NoteController {

    @Autowired
    private final NoteService service;

    @GetMapping
    public List<Note> getAllNotes() {
        return service.getAllNotes();
    }


    public NoteController( NoteService service) {
        this.service = service;
    }

    @GetMapping("/patient/{patId}")
    public List<Note> getNotesByPatient(@PathVariable Long patId) {
        return service.getNotesByPatient(patId);
    }

    @PostMapping
    public Note create(@RequestBody Note note) {
        return service.save(note);
    }

    @PutMapping("/{id}")
    public Note update(@PathVariable String id, @RequestBody Note note) {
        return service.update(id, note);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
