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
        Set<Etudiant> etudiants = new HashSet<>(Arrays.asList(new Etudiant(), new Etudiant()));
        departement.setEtudiants(etudiants);
    }

    @Test
    void testAddDepartement() {
        when(departementRepository.save(departement)).thenReturn(departement);
        Departement savedDepartement = departementService.addDepartement(departement);
        assertNotNull(savedDepartement);
        assertEquals("Informatique", savedDepartement.getNomDepart());
        assertEquals(2, savedDepartement.getEtudiants().size());  // Vérification du nombre d'étudiants
        verify(departementRepository, times(1)).save(departement);
    }

    @Test
    void testUpdateDepartement() {
        when(departementRepository.save(departement)).thenReturn(departement);
        departement.setNomDepart("Génie Informatique");
        Departement updatedDepartement = departementService.updateDepartement(departement);
        assertNotNull(updatedDepartement);
        assertEquals("Génie Informatique", updatedDepartement.getNomDepart());
        verify(departementRepository, times(1)).save(departement);
    }

    @Test
    void testDeleteDepartement() {
        when(departementRepository.findById(1)).thenReturn(Optional.of(departement));
        doNothing().when(departementRepository).delete(departement);
        departementService.deleteDepartement(1);
        verify(departementRepository, times(1)).delete(departement);
    }

    @Test
    void testDepartementStatus() {
        departement.setEtudiants(new HashSet<>(Arrays.asList(new Etudiant())));
        when(departementRepository.findById(1)).thenReturn(Optional.of(departement));
        String status = departementService.getDepartementStatus(1);
        assertEquals("Département dépeuplé", status);

        departement.setEtudiants(new HashSet<>(Arrays.asList(new Etudiant(), new Etudiant(), new Etudiant(), new Etudiant(), new Etudiant())));
        status = departementService.getDepartementStatus(1);
        assertEquals("Département normal", status);
    }
}
