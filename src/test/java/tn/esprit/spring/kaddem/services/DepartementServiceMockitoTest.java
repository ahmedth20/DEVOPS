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
import java.util.List;
import java.util.Map;

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
        departement = new Departement("Informatique");
        departement.setIdDepart(1);
        departement.setEtudiants(new HashSet<>(Arrays.asList(new Etudiant(), new Etudiant(), new Etudiant())));
    }

    @Test
    void testAddDepartement() {
        when(departementRepository.save(departement)).thenReturn(departement);
        Departement savedDepartement = departementService.addDepartement(departement);
        assertNotNull(savedDepartement);
        assertEquals("Informatique", savedDepartement.getNomDepart());
        verify(departementRepository, times(1)).save(departement);
    }

    @Test
    void testUpdateDepartement() {
        when(departementRepository.save(departement)).thenReturn(departement);
        departement.setNomDepart("Informatique Avancée");
        Departement updatedDepartement = departementService.updateDepartement(departement);
        assertNotNull(updatedDepartement);
        assertEquals("Informatique Avancée", updatedDepartement.getNomDepart());
        verify(departementRepository, times(1)).save(departement);
    }

    @Test
    void testRetrieveDepartement() {
        when(departementRepository.findById(1)).thenReturn(Optional.of(departement));
        Departement retrievedDepartement = departementService.retrieveDepartement(1);
        assertNotNull(retrievedDepartement);
        assertEquals("Informatique", retrievedDepartement.getNomDepart());
        verify(departementRepository, times(1)).findById(1);
    }

    @Test
    void testDeleteDepartement() {
        when(departementRepository.findById(1)).thenReturn(Optional.of(departement));
        doNothing().when(departementRepository).delete(departement);
        departementService.deleteDepartement(1);
        verify(departementRepository, times(1)).delete(departement);
    }



    @Test
    void testGetNombreEtudiantsParDepartement() {
        when(departementRepository.findAll()).thenReturn(Arrays.asList(departement));
        Map<String, Long> stats = departementService.getNombreEtudiantsParDepartement();
        assertNotNull(stats);
        assertEquals(1, stats.size());
        assertEquals(3, stats.get("Informatique"));
    }

    @Test
    void testFilterDepartements() {
        when(departementRepository.filterDepartements("Informatique", 2)).thenReturn(Arrays.asList(departement));
        List<Departement> filteredDepartements = departementService.filterDepartements("Informatique", 2);
        assertNotNull(filteredDepartements);
        assertEquals(1, filteredDepartements.size());
        verify(departementRepository, times(1)).filterDepartements("Informatique", 2);
    }
}
