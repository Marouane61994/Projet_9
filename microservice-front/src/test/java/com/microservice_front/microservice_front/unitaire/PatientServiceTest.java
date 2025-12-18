package com.microservice_front.microservice_front.unitaire;

import com.medilabo.front.service.PatientService;
import com.medilabo.front.model.Patient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PatientServiceTest {

    private RestClient.Builder mockBuilder;
    private RestClient mockRestClient;

    private RestClient.RequestHeadersUriSpec<?> mockHeadersUriSpec;
    private RestClient.RequestBodyUriSpec mockBodyUriSpec;
    private RestClient.ResponseSpec mockResponseSpec;

    private PatientService patientService;

    @BeforeEach
    void setup() {

        mockBuilder = mock(RestClient.Builder.class);
        mockRestClient = mock(RestClient.class);

        mockHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        mockBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        mockResponseSpec = mock(RestClient.ResponseSpec.class);

        when(mockBuilder.baseUrl(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.build()).thenReturn(mockRestClient);

       // patientService = new PatientService(mockBuilder, "http://gateway");
    }

    // =============================
    //         GET ALL PATIENTS
    // =============================
    @Test
    void getAllPatients_success() {

        Patient p1 = new Patient(1L, "Doe", "John", "1990-01-01", "M", "Paris", "0102030405");
        Patient p2 = new Patient(2L, "Smith", "Anna", "1985-05-10", "F", "Lyon", "0606060606");

        Patient[] patientsArray = {p1, p2};

        //when(mockRestClient.get()).thenReturn(mockHeadersUriSpec);
      //  when(mockHeadersUriSpec.uri("/patients")).thenReturn(mockHeadersUriSpec);
        when(mockHeadersUriSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.body(Patient[].class)).thenReturn(patientsArray);

        List<Patient> result = patientService.getAllPatients();

        assertEquals(2, result.size());
        assertEquals("Doe", result.get(0).getNom());
        assertEquals("Smith", result.get(1).getNom());
    }

    // =============================
    //          GET BY ID
    // =============================
    @Test
    void getPatientById_success() {

        Patient patient = new Patient(1L, "Doe", "John", "1990-01-01", "M", "Paris", "0101010101");

        //when(mockRestClient.get()).thenReturn(mockHeadersUriSpec);
//when(mockHeadersUriSpec.uri("/patients/{id}", 1L)).thenReturn(mockHeadersUriSpec);
        when(mockHeadersUriSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.body(Patient.class)).thenReturn(patient);

        Patient result = patientService.getPatientById(1L);

        assertNotNull(result);
        assertEquals("Doe", result.getNom());
    }

    // =============================
    //         UPDATE PATIENT
    // =============================
    @Test
    void updatePatient_success() {

        Long id = 5L;
        Patient updated = new Patient(id, "Martin", "Paul", "1975-11-11", "M", "Nice", "0707070707");

        when(mockRestClient.put()).thenReturn(mockBodyUriSpec);
        when(mockBodyUriSpec.uri("/patients/{id}", id)).thenReturn(mockBodyUriSpec);
        when(mockBodyUriSpec.body(updated)).thenReturn(mockBodyUriSpec);
        when(mockBodyUriSpec.retrieve()).thenReturn(mockResponseSpec);
        when(mockResponseSpec.toBodilessEntity()).thenReturn(null);

        patientService.updatePatient(id, updated);

        verify(mockRestClient).put();
        verify(mockBodyUriSpec).uri("/patients/{id}", id);
        verify(mockBodyUriSpec).body(updated);
        verify(mockBodyUriSpec).retrieve();
        verify(mockResponseSpec).toBodilessEntity();
    }
}
