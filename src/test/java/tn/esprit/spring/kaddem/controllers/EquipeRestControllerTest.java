package tn.esprit.spring.kaddem.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.*;
import org.springframework.test.web.servlet.*;
import tn.esprit.spring.kaddem.controllers.EquipeRestController;
import tn.esprit.spring.kaddem.entities.Equipe;
import tn.esprit.spring.kaddem.entities.Niveau;
import tn.esprit.spring.kaddem.services.IEquipeService;

import java.util.Arrays;
import java.util.List;

@WebMvcTest(EquipeRestController.class)
public class EquipeRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IEquipeService equipeService;

    @Test
    void testGetEquipes() throws Exception {
        List<Equipe> equipes = Arrays.asList(new Equipe("Equipe A", Niveau.JUNIOR), new Equipe("Equipe B", Niveau.SENIOR));
        when(equipeService.retrieveAllEquipes()).thenReturn(equipes);

        mockMvc.perform(get("/equipe/retrieve-all-equipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomEquipe").value("Equipe A"))
                .andExpect(jsonPath("$[0].niveau").value("JUNIOR"))
                .andExpect(jsonPath("$[1].nomEquipe").value("Equipe B"))
                .andExpect(jsonPath("$[1].niveau").value("SENIOR"));
    }

    @Test
    void testAddEquipe() throws Exception {
        Equipe equipe = new Equipe("Equipe A", Niveau.JUNIOR);
        when(equipeService.addEquipe(any(Equipe.class))).thenReturn(equipe);

        mockMvc.perform(post("/equipe/add-equipe")
                        .contentType("application/json")
                        .content("{\"nomEquipe\": \"Equipe A\", \"niveau\": \"JUNIOR\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomEquipe").value("Equipe A"))
                .andExpect(jsonPath("$.niveau").value("JUNIOR"));
    }

    @Test
    void testDeleteEquipe() throws Exception {
        doNothing().when(equipeService).deleteEquipe(1);

        mockMvc.perform(delete("/equipe/remove-equipe/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testRetrieveEquipe() throws Exception {
        Equipe equipe = new Equipe("Equipe A", Niveau.JUNIOR);
        when(equipeService.retrieveEquipe(1)).thenReturn(equipe);

        mockMvc.perform(get("/equipe/retrieve-equipe/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomEquipe").value("Equipe A"))
                .andExpect(jsonPath("$.niveau").value("JUNIOR"));
    }

    @Test
    void testUpdateEquipe() throws Exception {
        Equipe equipe = new Equipe("Equipe A", Niveau.JUNIOR);
        when(equipeService.updateEquipe(any(Equipe.class))).thenReturn(equipe);

        mockMvc.perform(put("/equipe/update-equipe")
                        .contentType("application/json")
                        .content("{\"nomEquipe\": \"Equipe A\", \"niveau\": \"JUNIOR\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomEquipe").value("Equipe A"))
                .andExpect(jsonPath("$.niveau").value("JUNIOR"));
    }
}
