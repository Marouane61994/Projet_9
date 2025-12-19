package com.medilabo.patient.unitaire;

import com.medilabo.patient.model.Patient;
import com.medilabo.patient.repository.PatientRepository;
import com.medilabo.patient.service.PatientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository repository;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;

    @BeforeEach
    void setUp() {
        patient = new Patient();
        patient.setId(1L);
        patient.setPrenom("Jean");
        patient.setNom("Dupont");
    }

    @Test
    void testGetAllPatients() {
        // Arrange
        when(repository.findAll()).thenReturn(Arrays.asList(patient));

        // Act
        List<Patient> result = patientService.getAllPatients();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Jean", result.get(0).getPrenom());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testGetPatientById_Success() {
        // Arrange
        when(repository.findById(1L)).thenReturn(Optional.of(patient));

        // Act
        Patient result = patientService.getPatientById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetPatientById_NotFound() {
        // Arrange
        when(repository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            patientService.getPatientById(2L);
        });

        assertTrue(exception.getMessage().contains("Patient non trouvé"));
    }

    @Test
    void testSavePatient() {
        // Arrange
        when(repository.save(any(Patient.class))).thenReturn(patient);

        // Act
        Patient saved = patientService.savePatient(new Patient());

        // Assert
        assertNotNull(saved);
        assertEquals("Dupont", saved.getNom());
        verify(repository, times(1)).save(any(Patient.class));
    }

    @Test
    void testUpdatePatient() {
        // Arrange
        Patient updatedDetails = new Patient();
        updatedDetails.setPrenom("Marc");
        updatedDetails.setNom("Durand");

        when(repository.findById(1L)).thenReturn(Optional.of(patient));
        when(repository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Patient result = patientService.updatePatient(1L, updatedDetails);

        // Assert
        assertEquals("Marc", result.getPrenom());
        assertEquals("Durand", result.getNom());
        verify(repository).save(patient); // On vérifie que l'objet existant a été sauvegardé avec les modifs
    }
}