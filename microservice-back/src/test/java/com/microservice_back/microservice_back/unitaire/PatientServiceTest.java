package com.microservice_back.microservice_back.unitaire;

import com.medilabo.patient.model.Genre;
import com.medilabo.patient.model.Patient;
import com.medilabo.patient.repository.PatientRepository;
import com.medilabo.patient.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PatientServiceTest {

    @Mock
    private PatientRepository repository;

    @InjectMocks
    private PatientService service;

    private Patient patient;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        patient = new Patient(
                1L,
                "Dupont",
                "Jean",
                "1980-01-01",
                Genre.HOMME,
                "1 rue de Paris",
                "0102030405"
        );
    }

    /**
     * Test getAllPatients()
     */
    @Test
    void getAllPatients_shouldReturnList() {
        // Arrange
        List<Patient> patients = Collections.singletonList(patient);
        when(repository.findAll()).thenReturn(patients);

        // Act
        List<Patient> result = service.getAllPatients();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNom()).isEqualTo("Dupont");
        verify(repository, times(1)).findAll();
    }

    /**
     * Test getPatientById() - success
     */
    @Test
    void getPatientById_shouldReturnPatient() {
        when(repository.findById(1L)).thenReturn(Optional.of(patient));

        Patient result = service.getPatientById(1L);

        assertThat(result.getNom()).isEqualTo("Dupont");
        verify(repository).findById(1L);
    }

    /**
     * Test getPatientById() - not found
     */
    @Test
    void getPatientById_shouldThrowException_whenNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.getPatientById(999L));

        assertThat(exception.getMessage()).isEqualTo("Patient non trouvé : 999");
        verify(repository).findById(999L);
    }

    /**
     * Test savePatient()
     */
    @Test
    void savePatient_shouldSaveAndReturnPatient() {
        when(repository.save(patient)).thenReturn(patient);

        Patient result = service.savePatient(patient);

        assertThat(result).isEqualTo(patient);
        verify(repository).save(patient);
    }

    /**
     * Test updatePatient() - success
     */
    @Test
    void updatePatient_shouldUpdateFields() {
        Patient newData = new Patient(
                null,
                "Martin",
                "Paul",
                "1990-02-02",
                Genre.HOMME,
                "2 rue Lyon",
                "0607080910"
        );

        when(repository.findById(1L)).thenReturn(Optional.of(patient));
        when(repository.save(any(Patient.class))).thenReturn(patient);

        Patient result = service.updatePatient(1L, newData);

        assertThat(result.getNom()).isEqualTo("Martin");
        assertThat(result.getPrenom()).isEqualTo("Paul");
        assertThat(result.getDateNaissance()).isEqualTo("1990-02-02");
        assertThat(result.getAdresse()).isEqualTo("2 rue Lyon");
        assertThat(result.getTelephone()).isEqualTo("0607080910");

        verify(repository).findById(1L);
        verify(repository).save(patient);
    }

    /**
     * Test updatePatient() - not found
     */
    @Test
    void updatePatient_shouldThrowException_whenNotFound() {
        Patient data = new Patient();
        when(repository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> service.updatePatient(999L, data));

        assertThat(exception.getMessage()).isEqualTo("Patient non trouvé : 999");
        verify(repository).findById(999L);
        verify(repository, never()).save(any());
    }
}

