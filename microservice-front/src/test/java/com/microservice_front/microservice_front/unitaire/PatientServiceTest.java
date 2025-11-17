package com.microservice_front.microservice_front.unitaire;

import com.medilabo.front.Service.PatientService;
import com.medilabo.front.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PatientServiceTest {

    @Mock
    private RestClient.Builder builder;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @InjectMocks
    private PatientService patientService;

    private Patient patient1;
    private Patient patient2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(builder.baseUrl(anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(restClient);

        patientService = new PatientService(builder, "http://gateway");

        // 🧱 Création de patients de test
        patient1 = new Patient();
        patient1.setId(1L);
        patient1.setPrenom("Alice");
        patient1.setNom("Dupont");
        patient1.setDateNaissance(String.valueOf(LocalDate.of(1990, 5, 10)));
        patient1.setGenre("F");

        patient2 = new Patient();
        patient2.setId(2L);
        patient2.setPrenom("Bob");
        patient2.setPrenom("Martin");
        patient2.setDateNaissance(String.valueOf(LocalDate.of(1985, 3, 15)));
        patient2.setGenre("M");
    }

    @Test
    void testGetAllPatients() {
        Patient[] responseArray = { patient1, patient2 };

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/patients")).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(Patient[].class)).thenReturn(responseArray);

        List<Patient> patients = patientService.getAllPatients();

        assertThat(patients).hasSize(2);
        assertThat(patients.get(0).getPrenom()).isEqualTo("Alice");
        verify(restClient).get();
    }

    @Test
    void testGetPatientById() {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/patients/{id}", 1L)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(Patient.class)).thenReturn(patient1);

        Patient result = patientService.getPatientById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getNom()).isEqualTo("Dupont");
        verify(restClient).get();
    }

    @Test
    void testUpdatePatient() {
        when(restClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri("/patients/{id}", 1L)).thenReturn(requestBodySpec);
        when(requestBodySpec.body(patient1)).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(null);

        patientService.updatePatient(1L, patient1);

        verify(restClient).put();
        verify(requestBodySpec).body(patient1);
    }
}
