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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    private Patient patient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        patient = new Patient();
        patient.setId(1L);
        patient.setNom("Dupont");
        patient.setPrenom("Jean");
        patient.setDateNaissance("1990-01-01");
        patient.setGenre(Genre.HOMME);
        patient.setAdresse("10 rue de Paris");
        patient.setTelephone("0102030405");
    }

    @Test
    void shouldReturnAllPatients() {
        when(patientRepository.findAll()).thenReturn(List.of(patient));

        List<Patient> result = patientService.getAllPatients();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNom()).isEqualTo("Dupont");
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnPatientById() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        Patient result = patientService.getPatientById(1L); // ✅

        assertThat(result).isNotNull();
        assertThat(result.getPrenom()).isEqualTo("Jean");
        verify(patientRepository, times(1)).findById(1L);
    }


    @Test
    void shouldSavePatient() {
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        Patient saved = patientService.savePatient(patient);

        assertThat(saved.getNom()).isEqualTo("Dupont");
        verify(patientRepository, times(1)).save(patient);
    }


}

