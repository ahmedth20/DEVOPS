package tn.esprit.spring.kaddem.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.entities.Option;
import tn.esprit.spring.kaddem.services.IEtudiantService;

import java.util.Arrays;
import java.util.List;

class EtudiantRestControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IEtudiantService etudiantService;

    @InjectMocks
    private EtudiantRestController etudiantRestController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(etudiantRestController).build();
    }

    @Test
    void testGetEtudiants() throws Exception {
        List<Etudiant> etudiants = Arrays.asList(
                new Etudiant(1, "Ali", "Ben Salah", Option.SE),
                new Etudiant(2, "Sami", "Trabelsi", Option.SIM)
        );
        when(etudiantService.retrieveAllEtudiants()).thenReturn(etudiants);

        mockMvc.perform(get("/etudiant/retrieve-all-etudiants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    void testRetrieveEtudiant() throws Exception {
        Etudiant e = new Etudiant(1, "Ali", "Ben Salah", Option.SE);
        when(etudiantService.retrieveEtudiant(1)).thenReturn(e);

        mockMvc.perform(get("/etudiant/retrieve-etudiant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomE").value("Ali"));
    }

    @Test
    void testAddEtudiant() throws Exception {
        Etudiant e = new Etudiant(1, "Ali", "Ben Salah", Option.SE);
        when(etudiantService.addEtudiant(any(Etudiant.class))).thenReturn(e);

        mockMvc.perform(post("/etudiant/add-etudiant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomE\":\"Ali\",\"prenomE\":\"Ben Salah\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomE").value("Ali"));
    }

    @Test
    void testRemoveEtudiant() throws Exception {
        doNothing().when(etudiantService).removeEtudiant(1);

        mockMvc.perform(delete("/etudiant/remove-etudiant/1"))
                .andExpect(status().isOk());

        verify(etudiantService, times(1)).removeEtudiant(1);
    }

    @Test
    void testUpdateEtudiant() throws Exception {
        Etudiant e = new Etudiant(1, "Ali", "Ben Salah", Option.SE);
        when(etudiantService.updateEtudiant(any(Etudiant.class))).thenReturn(e);

        mockMvc.perform(put("/etudiant/update-etudiant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idEtudiant\":1,\"nomE\":\"Ali\",\"prenomE\":\"Ben Salah\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomE").value("Ali"));
    }
}
