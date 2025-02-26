package tn.esprit.spring.kaddem.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.kaddem.entities.Contrat;
import tn.esprit.spring.kaddem.entities.Equipe;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.entities.Niveau;
import tn.esprit.spring.kaddem.repositories.EquipeRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipeServiceImplTest {

    @Mock
    private EquipeRepository equipeRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    @InjectMocks
    private EquipeServiceImpl equipeService;

    private Equipe equipe;

    @BeforeEach
    void setUp() {
        equipe = new Equipe();
        equipe.setIdEquipe(1);
        equipe.setNomEquipe("Team A");
        equipe.setNiveau(Niveau.JUNIOR);
    }

    @Test
    void testRetrieveAllEquipes() {
        List<Equipe> equipes = Arrays.asList(equipe);
        when(equipeRepository.findAll()).thenReturn(equipes);
        List<Equipe> result = equipeService.retrieveAllEquipes();
        assertEquals(1, result.size());
    }

    @Test
    void testAddEquipe() {
        when(equipeRepository.save(equipe)).thenReturn(equipe);
        Equipe result = equipeService.addEquipe(equipe);
        assertNotNull(result);
        assertEquals("Team A", result.getNomEquipe());
    }

    @Test
    void testDeleteEquipe() {
        when(equipeRepository.findById(1)).thenReturn(Optional.of(equipe));
        doNothing().when(equipeRepository).delete(equipe);
        equipeService.deleteEquipe(1);
        verify(equipeRepository, times(1)).delete(equipe);
    }

    @Test
    void testRetrieveEquipe() {
        when(equipeRepository.findById(1)).thenReturn(Optional.of(equipe));
        Equipe result = equipeService.retrieveEquipe(1);
        assertNotNull(result);
        assertEquals("Team A", result.getNomEquipe());
    }

    @Test
    void testUpdateEquipe() {
        when(equipeRepository.save(equipe)).thenReturn(equipe);
        Equipe result = equipeService.updateEquipe(equipe);
        assertEquals("Team A", result.getNomEquipe());
    }

    @Test
    void testEvoluerEquipes() {
        Set<Etudiant> etudiants = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            Etudiant etudiant = new Etudiant();
            Contrat contrat = new Contrat();
            contrat.setDateFinContrat(java.sql.Date.valueOf(LocalDate.now().minusYears(2)));
            contrat.setArchive(false);
            etudiant.setContrats(Set.of(contrat));
            etudiants.add(etudiant);
        }
        equipe.setEtudiants(etudiants);
        when(equipeRepository.findAll()).thenReturn(List.of(equipe));
        equipeService.evoluerEquipes();
        verify(equipeRepository).save(equipe);
    }

    @Test
    void testRetrieveEquipesWithActiveContrats() {
        Contrat contrat = new Contrat();
        contrat.setDateFinContrat(java.sql.Date.valueOf(LocalDate.now().minusYears(2)));
        contrat.setArchive(false);

        Etudiant etudiant = new Etudiant();
        etudiant.setContrats(Set.of(contrat));

        Set<Etudiant> etudiantsSet = new HashSet<>();
        etudiantsSet.add(etudiant);
        equipe.setEtudiants(etudiantsSet);

        when(equipeRepository.findAll()).thenReturn(List.of(equipe));
        List<Equipe> result = equipeService.retrieveEquipesWithActiveContrats();
        assertFalse(result.isEmpty());
    }

    @Test
    void testRetrieveEquipesByMinStudents() {
        equipe.setEtudiants(new HashSet<>(List.of(new Etudiant(), new Etudiant())));
        when(equipeRepository.findAll()).thenReturn(List.of(equipe));
        List<Equipe> result = equipeService.retrieveEquipesByMinStudents(1);
        assertEquals(1, result.size());
    }

    @Test
    void testCountActiveContractsInEquipe() {
        Contrat contrat = new Contrat();
        contrat.setDateFinContrat(java.sql.Date.valueOf(LocalDate.now().minusYears(2)));
        contrat.setArchive(false);

        Etudiant etudiant = new Etudiant();
        etudiant.setContrats(Set.of(contrat));
        equipe.setEtudiants(new HashSet<>(List.of(etudiant)));

        when(equipeRepository.findById(1)).thenReturn(Optional.of(equipe));
        long count = equipeService.countActiveContractsInEquipe(1);
        assertEquals(1, count);
    }

    @Test
    void testHasExpiredContracts() {
        Contrat contrat = new Contrat();
        contrat.setDateFinContrat(java.sql.Date.valueOf(LocalDate.now().minusDays(1)));
        contrat.setArchive(false);

        Etudiant etudiant = new Etudiant();
        etudiant.setContrats(Set.of(contrat));
        equipe.setEtudiants(new HashSet<>(List.of(etudiant)));

        when(equipeRepository.findById(1)).thenReturn(Optional.of(equipe));
        assertTrue(equipeService.hasExpiredContracts(1));
    }

    @Test
    void testRetrieveEquipesWithoutStudents() {
        equipe.setEtudiants(Collections.emptySet());
        when(equipeRepository.findAll()).thenReturn(List.of(equipe));
        List<Equipe> result = equipeService.retrieveEquipesWithoutStudents();
        assertEquals(1, result.size());
    }

    @Test
    void testRetrieveEquipesByMinLevel() {
        equipe.setNiveau(Niveau.EXPERT);
        when(equipeRepository.findAll()).thenReturn(List.of(equipe));
        List<Equipe> result = equipeService.retrieveEquipesByMinLevel(Niveau.SENIOR);
        assertEquals(1, result.size());
    }
}
