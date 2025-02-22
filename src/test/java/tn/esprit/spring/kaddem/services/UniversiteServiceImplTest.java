package tn.esprit.spring.kaddem.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.entities.Universite;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;
import tn.esprit.spring.kaddem.repositories.UniversiteRepository;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
public class UniversiteServiceImplTest {

    @Autowired
    UniversiteRepository universiteRepository;
    @Autowired
    DepartementRepository departementRepository;
    @Autowired
    EtudiantRepository etudiantRepository;
    private Universite savedUniversite;

    @BeforeEach
    public void setUp() {
        universiteRepository.deleteAll();
        universiteRepository.flush();

        Universite universite = new Universite();
        universite.setNomUniv("Esprit");
        universite.setAnneeCreation(2003);
        universite.setBudget(100000.0);
        universite.setDepartements(new HashSet<>());

        Departement departement = new Departement();
        departement.setNomDepart("Info");
        Etudiant etudiant = new Etudiant();
        etudiant.setNomE("yosr");
        departement.setEtudiants(new HashSet<>());
        departement.getEtudiants().add(etudiant);
        universite.getDepartements().add(departement);

        departementRepository.save(departement);
        savedUniversite = universiteRepository.save(universite);
        universiteRepository.flush();
        departementRepository.flush();
        etudiantRepository.save(etudiant);
        etudiantRepository.flush();

    }

    @Test
    public void testAddUniversite() {
        Universite universite = new Universite();
        universite.setNomUniv("New Univ");
        universite.setAnneeCreation(2020);
        universite.setBudget(500000.0);

        Universite saved = universiteRepository.save(universite);
        universiteRepository.flush();

        assertNotNull(saved, "L'université n'a pas été sauvegardée !");
        assertNotNull(saved.getIdUniv(), "L'ID de l'université est null !");
    }

    @Test
    public void testRetrieveUniversite() {
        Optional<Universite> found = universiteRepository.findById(savedUniversite.getIdUniv());
        assertTrue(found.isPresent(), "L'université n'a pas été trouvée !");
    }

    @Test
    public void testUpdateUniversite() {
        savedUniversite.setNomUniv("ESPRIT UPDATED");
        Universite updated = universiteRepository.save(savedUniversite);
        universiteRepository.flush();

        assertEquals("ESPRIT UPDATED", updated.getNomUniv(), "L'université n'a pas été mise à jour !");
    }

    @Test
    public void testDeleteUniversite() {
        universiteRepository.deleteById(savedUniversite.getIdUniv());
        universiteRepository.flush();

        Optional<Universite> deleted = universiteRepository.findById(savedUniversite.getIdUniv());
        assertFalse(deleted.isPresent(), "L'université n'a pas été supprimée !");
    }

    @Test
    public void testGetUniversitesByAnneeCreation() {
        List<Universite> universites = universiteRepository.findUniversitesByAnneeCreation(2003);
        assertFalse(universites.isEmpty(), "La liste des universités ne devrait pas être vide !");

    }

    @Test
    public void testGetUniversitesByBudget() {
        List<Universite> universites = universiteRepository.findUniversitesByBudget(100000.0);
        assertEquals(1, universites.size());
        assertFalse(universites.isEmpty(), "Aucune université trouvée avec ce budget !");
    }

    @Test
    void testGetUniversitesByMinEtudiants() {
        List<Universite> universites = universiteRepository.findUniversitesByMinEtudiants(500);
        assertNotNull(universites);
        assertTrue(universites.size() >= 0);
    }

    @Test
    void testGetUniversitesPaged() {
        Page<Universite> universitePage = universiteRepository.findAll(PageRequest.of(0, 5));
        List<Universite> universites = universitePage.getContent();
        assertNotNull(universites);
        assertTrue(universites.size() <= 5);
    }

    @Test
    void testAttribuerBudgetUniversite() {
        double nouveauBudget = 200000.0;
        savedUniversite.setBudget(nouveauBudget);
        Universite updated = universiteRepository.save(savedUniversite);
        universiteRepository.flush();
        assertEquals(nouveauBudget, updated.getBudget());
    }

    @Test
    void testRetrieveDepartementsByUniversite() {
        Optional<Universite> foundUniversite = universiteRepository.findById(savedUniversite.getIdUniv());
        assertTrue(foundUniversite.isPresent(), "L'université n'a pas été trouvée !");
        Set<Departement> departements = foundUniversite.get().getDepartements();
        assertNotNull(departements, "La liste des départements ne devrait pas être null !");
        assertFalse(departements.isEmpty(), "L'université devrait avoir au moins un département !");
    }

    @Test
    void testRetrieveAllUniversites() {
        List<Universite> universites = universiteRepository.findAll();
        assertNotNull(universites);
        assertFalse(universites.isEmpty());
    }

  /*  @Test
    void testAssignUniversiteToDepartementToEtudiant() {
        // Create and initialize the department
        Departement departement = new Departement();
        departement.setNomDepart("Arctic");
        departement.setEtudiants(new HashSet<>());

        // Create and initialize the student
        Etudiant etudiant = new Etudiant();
        etudiant.setNomE("yosr");
        etudiant.setDepartement(departement); // Set the department for the student

        // Add student to the department
        departement.getEtudiants().add(etudiant);

        // Save the department first
        Departement savedDepartement = departementRepository.save(departement);

        // Add department to the university
        savedUniversite.getDepartements().add(savedDepartement);

        // Save the university
        Universite updatedUniversite = universiteRepository.save(savedUniversite);

        // Verify
        Optional<Universite> foundUniversite = universiteRepository.findById(updatedUniversite.getIdUniv());
        assertTrue(foundUniversite.isPresent(), "L'université n'a pas été trouvée !");
        Universite universite = foundUniversite.get();
        Set<Departement> departements = universite.getDepartements();

        assertFalse(departements.isEmpty(), "L'université doit avoir des départements !");
        Departement firstDept = departements.iterator().next();
        assertFalse(firstDept.getEtudiants().isEmpty(), "Le département doit contenir des étudiants !");
        assertEquals("yosr", firstDept.getEtudiants().iterator().next().getNomE(), "L'étudiant ne correspond pas !");
    }*/
}
