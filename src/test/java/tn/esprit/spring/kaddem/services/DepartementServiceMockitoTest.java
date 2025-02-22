package tn.esprit.spring.kaddem.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepartementServiceMockitoTest {

    @Mock
    private DepartementRepository departementRepository;

    @InjectMocks
    private DepartementServiceImpl departementService;

    private Departement departement;

    @BeforeEach
    void setUp() {
        departement = new Departement();
        departement.setIdDepart(1);
        departement.setNomDepart("Informatique");

        // Initialisation des étudiants
        Set<Etudiant> etudiants = new HashSet<>(Arrays.asList(new Etudiant(), new Etudiant()));
        departement.setEtudiants(etudiants);
    }

    @Test
    void testAddDepartement() {
        // Simuler la méthode save() du repository
        when(departementRepository.save(departement)).thenReturn(departement);

        // Appel du service pour ajouter le département
        Departement savedDepartement = departementService.addDepartement(departement);

        // Vérification des résultats
        assertNotNull(savedDepartement);
        assertEquals("Informatique", savedDepartement.getNomDepart());
        assertEquals(2, savedDepartement.getEtudiants().size());  // Vérification du nombre d'étudiants
        verify(departementRepository, times(1)).save(departement);
    }

    @Test
    void testRetrieveAllDepartements() {
        // Simuler la méthode findAll() du repository
        when(departementRepository.findAll()).thenReturn(Arrays.asList(departement));

        // Appel du service pour récupérer tous les départements
        var departements = departementService.retrieveAllDepartements();

        // Vérification des résultats
        assertNotNull(departements);
        assertEquals(1, departements.size());
        verify(departementRepository, times(1)).findAll();
    }

    @Test
    void testRetrieveDepartement() {
        // Simuler la méthode findById() du repository
        when(departementRepository.findById(1)).thenReturn(Optional.of(departement));

        // Appel du service pour récupérer le département par ID
        Departement foundDepartement = departementService.retrieveDepartement(1);

        // Vérification des résultats
        assertNotNull(foundDepartement);
        assertEquals(1, foundDepartement.getIdDepart());
        assertEquals("Informatique", foundDepartement.getNomDepart());
        assertEquals(2, foundDepartement.getEtudiants().size());  // Vérification des étudiants
        verify(departementRepository, times(1)).findById(1);
    }

    @Test
    void testUpdateDepartement() {
        // Simuler la méthode save() du repository
        when(departementRepository.save(departement)).thenReturn(departement);

        // Mise à jour du département
        departement.setNomDepart("Génie Informatique");

        // Appel du service pour mettre à jour le département
        Departement updatedDepartement = departementService.updateDepartement(departement);

        // Vérification des résultats
        assertNotNull(updatedDepartement);
        assertEquals("Génie Informatique", updatedDepartement.getNomDepart());
        verify(departementRepository, times(1)).save(departement);
    }

    @Test
    void testDeleteDepartement() {
        // Simuler la méthode findById() du repository
        when(departementRepository.findById(1)).thenReturn(Optional.of(departement));
        // Simuler la méthode delete() du repository
        doNothing().when(departementRepository).delete(departement);

        // Appel du service pour supprimer le département
        departementService.deleteDepartement(1);

        // Vérification que la méthode delete a été appelée une fois
        verify(departementRepository, times(1)).delete(departement);
    }
}
