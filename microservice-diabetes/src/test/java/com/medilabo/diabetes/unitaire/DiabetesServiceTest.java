package com.medilabo.diabetes.unitaire;

import com.medilabo.diabetes.model.Note;
import com.medilabo.diabetes.model.Patient;
import com.medilabo.diabetes.repository.NoteRepository;
import com.medilabo.diabetes.repository.PatientRepository;
import com.medilabo.diabetes.service.DiabetesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiabetesServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private DiabetesService diabetesService;

    private Patient mockPatient;

    @BeforeEach
    void setUp() {
        mockPatient = new Patient();
        mockPatient.setId(1L);
        mockPatient.setPrenom("Test");
        mockPatient.setNom("Patient");
    }

    @Test
    void testCountTriggers_ShouldIgnoreAccentsAndCase() {
        // Arrange
        Note note = new Note();
        note.setNote("Le patient a des VERTIGES et son Hémoglobine A1C est élevée.");
        List<Note> notes = Collections.singletonList(note);

        // Act
        int count = diabetesService.countTriggers(notes);

        // Assert
        assertEquals(2, count);
    }

    @Test
    void testAssessRisk_None_Over30() {
        // Patient de 40 ans (né en 1985 si on est en 2025)
        mockPatient.setDateNaissance("1985-01-01");
        mockPatient.setGenre("M");

        when(patientRepository.getPatientById(1L)).thenReturn(mockPatient);
        when(noteRepository.getNotesByPatient(1L)).thenReturn(Collections.emptyList());

        String risk = diabetesService.assessDiabetesRisk(1L);
        assertEquals("None", risk);
    }

    @Test
    void testAssessRisk_Borderline_Over30_TwoTriggers() {
        mockPatient.setDateNaissance("1980-01-01");
        Note n1 = new Note(); n1.setNote("Poids");
        Note n2 = new Note(); n2.setNote("Taille");

        when(patientRepository.getPatientById(1L)).thenReturn(mockPatient);
        when(noteRepository.getNotesByPatient(1L)).thenReturn(Arrays.asList(n1, n2));

        String risk = diabetesService.assessDiabetesRisk(1L);
        assertEquals("Borderline", risk);
    }

    @Test
    void testAssessRisk_InDanger_YoungMale_ThreeTriggers() {
        // Homme de 25 ans (né en 2000)
        mockPatient.setDateNaissance("2000-01-01");
        mockPatient.setGenre("M");
        Note n1 = new Note(); n1.setNote("Fumeur");
        Note n2 = new Note(); n2.setNote("Cholestérol");
        Note n3 = new Note(); n3.setNote("Rechute");

        when(patientRepository.getPatientById(1L)).thenReturn(mockPatient);
        when(noteRepository.getNotesByPatient(1L)).thenReturn(Arrays.asList(n1, n2, n3));

        String risk = diabetesService.assessDiabetesRisk(1L);
        assertEquals("In Danger", risk);
    }

    @Test
    void testAssessRisk_EarlyOnset_YoungFemale_SevenTriggers() {
        // Femme de 28 ans (en 2026)
        mockPatient.setDateNaissance("1997-01-01");
        mockPatient.setGenre("F");

        // Ajout d'un 5ème argument (null ou la version minuscules) pour respecter le constructeur
        List<Note> notes = Arrays.asList(
                new Note(null, 1L, null, "Poids", "poids"),
                new Note(null, 1L, null, "Taille", "taille"),
                new Note(null, 1L, null, "Vertige", "vertige"),
                new Note(null, 1L, null, "Anormal", "anormal"),
                new Note(null, 1L, null, "Cholestérol", "cholestérol"),
                new Note(null, 1L, null, "Microalbumine", "microalbumine"),
                new Note(null, 1L, null, "Anticorps", "anticorps")
        );

        when(patientRepository.getPatientById(1L)).thenReturn(mockPatient);
        when(noteRepository.getNotesByPatient(1L)).thenReturn(notes);

        String risk = diabetesService.assessDiabetesRisk(1L);
        assertEquals("Early onset", risk);
    }
}