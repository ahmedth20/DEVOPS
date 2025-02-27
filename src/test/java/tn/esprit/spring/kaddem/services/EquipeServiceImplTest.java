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
        List<Equipe> equipes = new ArrayList<>(Collections.singletonList(equipe));
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

   /* @Test
    void testEvoluerEquipes() {
        // Créer un ensemble d'étudiants
        Set<Etudiant> etudiants = new HashSet<>();
        for (int i = 0; i < 3; i++) {
            Etudiant etudiant = new Etudiant();
            Contrat contrat = new Contrat();
            contrat.setDateFinContrat(java.sql.Date.valueOf(LocalDate.now().minusYears(2)));
            contrat.setArchive(false);
            etudiant.setContrats(new HashSet<>(Collections.singletonList(contrat)));
            etudiants.add(etudiant);
        }

        // Ajouter les étudiants à l'équipe
        equipe.setEtudiants(etudiants);

        // S'assurer que l'équipe a un niveau initial
        equipe.setNiveau(Niveau.JUNIOR);  // Remplacez par le niveau actuel de l'équipe

        // Simuler le retour de findAll
        when(equipeRepository.findAll()).thenReturn(Collections.singletonList(equipe));

        // Appeler la méthode à tester
        equipeService.evoluerEquipes();

        // Vérifier que l'équipe a été sauvegardée après l'évolution
        verify(equipeRepository).save(equipe);

        // Vous pouvez également ajouter une assertion pour vérifier si le niveau de l'équipe a évolué
        assertNotEquals(Niveau.SENIOR, equipe.getNiveau(), "Le niveau de l'équipe n'a pas évolué correctement.");
    }*/



    @Test
    void testRetrieveEquipesWithActiveContrats() {
        // Contrat actif
        Contrat contrat = new Contrat();
        LocalDate dateFin = LocalDate.now().plusYears(1); // Contrat actif
        contrat.setDateFinContrat(java.sql.Date.valueOf(dateFin));
        contrat.setArchive(false); // Assurez-vous que le contrat n'est pas archivé

        // Etudiant avec un contrat actif
        Etudiant etudiant = new Etudiant();
        etudiant.setContrats(new HashSet<>(Collections.singletonList(contrat)));

        Set<Etudiant> etudiantsSet = new HashSet<>();
        etudiantsSet.add(etudiant);
        equipe.setEtudiants(etudiantsSet);

        // Simulation du repository qui retourne l'équipe avec le contrat actif
        when(equipeRepository.findAll()).thenReturn(Collections.singletonList(equipe));

        // Appel de la méthode
        List<Equipe> result = equipeService.retrieveEquipesWithActiveContrats();

        // Test si l'équipe est bien présente dans le résultat
        assertFalse(result.isEmpty());
    }


    @Test
    void testRetrieveEquipesByMinStudents() {
        equipe.setEtudiants(new HashSet<>(Arrays.asList(new Etudiant(), new Etudiant())));
        when(equipeRepository.findAll()).thenReturn(Collections.singletonList(equipe));
        List<Equipe> result = equipeService.retrieveEquipesByMinStudents(1);
        assertEquals(1, result.size());
    }

    @Test
    void testCountActiveContractsInEquipe() {
        Contrat contrat = new Contrat();
        contrat.setDateFinContrat(java.sql.Date.valueOf(LocalDate.now().plusYears(1))); // Date future pour être actif
        contrat.setArchive(false);

        Etudiant etudiant = new Etudiant();
        etudiant.setContrats(new HashSet<>(Collections.singletonList(contrat)));

        Equipe equipe = new Equipe();
        equipe.setEtudiants(new HashSet<>(Collections.singletonList(etudiant)));

        when(equipeRepository.findById(1)).thenReturn(Optional.of(equipe));

        long count = equipeService.countActiveContractsInEquipe(1);

        assertEquals(1, count); // Vérifier qu'il y a bien un contrat actif
    }

    @Test
    void testHasExpiredContracts() {
        Contrat contrat = new Contrat();
        contrat.setDateFinContrat(java.sql.Date.valueOf(LocalDate.now().minusDays(1)));
        contrat.setArchive(false);

        Etudiant etudiant = new Etudiant();
        etudiant.setContrats(new HashSet<>(Collections.singletonList(contrat)));
        equipe.setEtudiants(new HashSet<>(Collections.singletonList(etudiant)));

        when(equipeRepository.findById(1)).thenReturn(Optional.of(equipe));

        // Vérification de l'expiration du contrat en comparant les LocalDate
        assertTrue(equipeService.hasExpiredContracts(1));
    }


    @Test
    void testRetrieveEquipesWithoutStudents() {
        equipe.setEtudiants(Collections.emptySet());
        when(equipeRepository.findAll()).thenReturn(Collections.singletonList(equipe));
        List<Equipe> result = equipeService.retrieveEquipesWithoutStudents();
        assertEquals(1, result.size());
    }

    @Test
    void testRetrieveEquipesByMinLevel() {
        equipe.setNiveau(Niveau.EXPERT);
        when(equipeRepository.findAll()).thenReturn(Collections.singletonList(equipe));
        List<Equipe> result = equipeService.retrieveEquipesByMinLevel(Niveau.SENIOR);
        assertEquals(1, result.size());
    }
}
