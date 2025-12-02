package com.microservice_front.microservice_front.integration;

import com.medilabo.front.Controller.DiabetesController;
import com.medilabo.front.Service.DiabetesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ContextConfiguration(classes = com.medilabo.front.FrontApplication.class)
@WebMvcTest(DiabetesController.class)
class DiabetesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DiabetesService diabetesService;

    @Test
    void getDiabetesReport_success() throws Exception {

        Map<String, Object> fakeReport = Map.of(
                "prenom", "John",
                "nom", "Doe",
                "patientId", 1L,
                "genre", "M",
                "dateNaissance", "1980-01-01",
                "age", 45,
                "nombreDeclencheurs", 6,
                "niveauRisque", "Borderline"
        );

        when(diabetesService.getDiabetesReport(1L)).thenReturn(fakeReport);

        mockMvc.perform(get("/patients/1/assess"))
                .andExpect(status().isOk())
                .andExpect(view().name("diabetes-report"))
                .andExpect(model().attributeExists("report"))
                .andExpect(model().attribute("patientId", 1L));
    }

    @Test
    void getDiabetesReport_error() throws Exception {

        when(diabetesService.getDiabetesReport(1L)).thenReturn(null);

        mockMvc.perform(get("/patients/1/assess"))
                .andExpect(status().isOk())
                .andExpect(view().name("diabetes-report"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error",
                        "Impossible de récupérer le rapport de diabète pour ce patient."));
    }
}
