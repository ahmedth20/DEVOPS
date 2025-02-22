package tn.esprit.spring.kaddem.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;

import javax.transaction.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")  // Utilise le profil de test
@Transactional
public class DepartementServiceTest {

    @Autowired
    DepartementRepository departementRepository;

    private Departement savedDepartement;

    @BeforeEach
    public void setUp() {
        // Créer un département avec des informations de base
        Departement departement = new Departement("Informatique");
        savedDepartement = departementRepository.save(departement);
        departementRepository.flush();  // Assurez-vous que l'objet est bien sauvegardé
    }

    @Test
    public void testAjoutDepartement() {
        Departement departement = new Departement("Gestion");
        Departement saved = departementRepository.save(departement);
        departementRepository.flush();

        assertNotNull(saved, "Le département n'a pas été sauvegardé !");
        assertNotNull(saved.getIdDepart(), "L'ID du département est null !");
    }

    @Test
    public void testGetDepartement() {
        Departement found = departementRepository.findById(savedDepartement.getIdDepart())
                .orElseThrow(() -> new IllegalArgumentException("Département non trouvé"));
        assertEquals("Informatique", found.getNomDepart(), "Le département n'a pas été trouvé !");
    }

    @Test
    public void testUpdateDepartement() {
        savedDepartement.setNomDepart("Informatique Avancée");
        Departement updated = departementRepository.save(savedDepartement);
        departementRepository.flush();

        assertEquals("Informatique Avancée", updated.getNomDepart(), "Le département n'a pas été mis à jour !");
    }

    @Test
    public void testDeleteDepartement() {
        departementRepository.deleteById(savedDepartement.getIdDepart());
        departementRepository.flush();

        assertFalse(departementRepository.findById(savedDepartement.getIdDepart()).isPresent(),
                "Le département n'a pas été supprimé !");
    }
}
