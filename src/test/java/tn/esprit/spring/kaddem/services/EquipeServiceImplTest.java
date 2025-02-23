package tn.esprit.spring.kaddem.services;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.*;
import tn.esprit.spring.kaddem.entities.*;
import tn.esprit.spring.kaddem.repositories.EquipeRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;

import java.util.*;

@ExtendWith(MockitoExtension.class)  // Utilisation de MockitoExtension pour JUnit 5
public class EquipeServiceImplTest {

    @InjectMocks
    private EquipeServiceImpl equipeService;  // Injection du service dans le test

    @Mock
    private EquipeRepository equipeRepository;  // Mock du repository des équipes

    @Mock
    private EtudiantRepository etudiantRepository;  // Mock du repository des étudiants

    private Etudiant etudiant1;
    private Etudiant etudiant2;

    // Initialisation des objets avant chaque test
    @BeforeEach
    void setUp() {
        etudiant1 = new Etudiant("Etudiant A", "email1@example.com");
        etudiant2 = new Etudiant("Etudiant B", "email2@example.com");
    }

    @Test
    void testRetrieveAllEquipes() {
        List<Equipe> equipes = Arrays.asList(new Equipe("Equipe A", Niveau.JUNIOR), new Equipe("Equipe B", Niveau.SENIOR));
        when(equipeRepository.findAll()).thenReturn(equipes);

        List<Equipe> result = equipeService.retrieveAllEquipes();
        assertEquals(2, result.size());
        assertEquals("Equipe A", result.get(0).getNomEquipe());
    }

    @Test
    void testAddEquipe() {
        Equipe equipe = new Equipe("Equipe A", Niveau.JUNIOR);
        when(equipeRepository.save(any(Equipe.class))).thenReturn(equipe);

        Equipe result = equipeService.addEquipe(equipe);
        assertNotNull(result);
        assertEquals("Equipe A", result.getNomEquipe());
    }

    @Test
    void testDeleteEquipe() {
        Equipe equipe = new Equipe("Equipe A", Niveau.JUNIOR);
        when(equipeRepository.findById(1)).thenReturn(Optional.of(equipe));
        doNothing().when(equipeRepository).delete(equipe);

        equipeService.deleteEquipe(1);
        verify(equipeRepository, times(1)).delete(equipe);
    }

    @Test
    void testRetrieveEquipe() {
        Equipe equipe = new Equipe("Equipe A", Niveau.JUNIOR);
        when(equipeRepository.findById(1)).thenReturn(Optional.of(equipe));

        Equipe result = equipeService.retrieveEquipe(1);
        assertEquals("Equipe A", result.getNomEquipe());
    }

    @Test
    void testUpdateEquipe() {
        Equipe equipe = new Equipe("Equipe A", Niveau.JUNIOR);
        when(equipeRepository.save(any(Equipe.class))).thenReturn(equipe);

        Equipe result = equipeService.updateEquipe(equipe);
        assertEquals("Equipe A", result.getNomEquipe());
    }

   /* @Test
    void testEvoluerEquipes() {
        // Création des équipes de test
        Equipe equipe1 = new Equipe("Equipe A", Niveau.JUNIOR);
        Equipe equipe2 = new Equipe("Equipe B", Niveau.SENIOR);

        // Liste d'étudiants factices
        List<Etudiant> etudiants = Arrays.asList(etudiant1, etudiant2);

        // Mock des repositories
        when(equipeRepository.findAll()).thenReturn(Arrays.asList(equipe1, equipe2));
        when(etudiantRepository.findAll()).thenReturn(etudiants);

        // Exécution de la méthode à tester
        equipeService.evoluerEquipes();

        // Vérifications
        verify(equipeRepository, times(1)).save(equipe1);  // Equipe A devrait être mise à jour
        verify(equipeRepository, times(1)).save(equipe2);  // Equipe B devrait aussi être mise à jour
        verify(etudiantRepository, times(1)).findAll();  // Vérifie que findAll est appelé pour récupérer les étudiants
    }*/
}
