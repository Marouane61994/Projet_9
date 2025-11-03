package com.medilabo.note.service;

import com.medilabo.note.model.Note;
import com.medilabo.note.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    @Autowired
    private final NoteRepository repository;

    public List<Note> getAllNotes() {
        return repository.findAll();
    }

    public NoteService(NoteRepository repository) {
        this.repository = repository;
    }

    public List<Note> getNotesByPatient(Long patId) {
        return repository.findByPatId(patId);
    }

    public Note save(Note note) {
        return repository.save(note);
    }

    public Note update(String id, Note note) {
        note.setId(id);
        return repository.save(note);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
}


