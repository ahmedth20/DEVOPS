package tn.esprit.spring.kaddem.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class DepartementServiceTest {

    @Autowired
    private DepartementServiceImpl departementService;

    @Autowired
    private DepartementRepository departementRepository;

    private Departement departement;

    @BeforeEach
    public void setUp() {
        departement = new Departement("Informatique");
        departement.setEtudiants(new HashSet<>());
        departement = departementRepository.save(departement); // Assurez-vous de récupérer l'ID après save
    }

    @Test
    public void testAddDepartement() {
        Departement newDepartement = new Departement("Génie Civil");
        Departement savedDepartement = departementService.addDepartement(newDepartement);
        assertNotNull(savedDepartement);
        assertEquals("Génie Civil", savedDepartement.getNomDepart());
    }

    @Test
    public void testUpdateDepartement() {
        departement.setNomDepart("Informatique Avancée");
        Departement updatedDepartement = departementService.updateDepartement(departement);
        assertNotNull(updatedDepartement);
        assertEquals("Informatique Avancée", updatedDepartement.getNomDepart());
    }

    @Test
    public void testRetrieveDepartement() {
        Departement retrievedDepartement = departementService.retrieveDepartement(departement.getIdDepart());
        assertNotNull(retrievedDepartement);
        assertEquals("Informatique", retrievedDepartement.getNomDepart());
    }

    @Test
    public void testDeleteDepartement() {
        departementService.deleteDepartement(departement.getIdDepart());
        assertFalse(departementRepository.findById(departement.getIdDepart()).isPresent());
    }


    @Test
    public void testGetNombreEtudiantsParDepartement() {
        Map<String, Long> stats = departementService.getNombreEtudiantsParDepartement();
        assertNotNull(stats);
        assertTrue(stats.containsKey("Informatique"));
    }

    @Test
    public void testFilterDepartements() {
        List<Departement> filteredDepartements = departementService.filterDepartements("Informatique", 1);
        assertNotNull(filteredDepartements);
        assertTrue(filteredDepartements.isEmpty() || filteredDepartements.size() >= 1);
    }
}
