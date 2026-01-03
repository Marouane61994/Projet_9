package com.medilabo.patient.service;

import com.medilabo.patient.model.Patient;
import com.medilabo.patient.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository repository;

    public PatientService(PatientRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public List<Patient> getAllPatients() {
        return repository.findAll();
    }
    @Transactional
    public Patient getPatientById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé : " + id));
    }
    @Transactional
    public Patient savePatient(Patient patient) {
        return repository.save(patient);
    }
    @Transactional
    public Patient updatePatient(Long id, Patient updatedPatient) {
        Patient existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé : " + id));

        existing.setPrenom(updatedPatient.getPrenom());
        existing.setNom(updatedPatient.getNom());
        existing.setDateNaissance(updatedPatient.getDateNaissance());
        existing.setGenre(updatedPatient.getGenre());
        existing.setAdresse(updatedPatient.getAdresse());
        existing.setTelephone(updatedPatient.getTelephone());

        return repository.save(existing);
    }

}
