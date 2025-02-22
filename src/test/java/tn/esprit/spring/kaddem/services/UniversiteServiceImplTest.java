package tn.esprit.spring.kaddem.services;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import tn.esprit.spring.kaddem.entities.Universite;
import tn.esprit.spring.kaddem.repositories.UniversiteRepository;

import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
public class UniversiteServiceImplTest {
    @Autowired
    UniversiteRepository universiteRepository;

    private Universite savedUniversite;

    @BeforeEach
    public void setUp() {
        Universite universite = new Universite();
        universite.setNomUniv("Esprit");
        universite.setAnneeCreation(2003);
        universite.setBudget(100000.0);
        savedUniversite = universiteRepository.save(universite);
        universiteRepository.flush();
    }

    @Test
    public void testAjoutUniversite() {
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
    public void testGetUniversite() {
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
}
