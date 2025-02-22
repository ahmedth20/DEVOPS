package tn.esprit.spring.kaddem.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.entities.Universite;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;
import tn.esprit.spring.kaddem.repositories.UniversiteRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UniversiteServiceImplMockTest {
    @Mock
    private UniversiteRepository universiteRepository;
    @Mock
    private DepartementRepository departementRepository;

    @Mock
    private EtudiantRepository etudiantRepository;

    private Universite universite;
    private Departement departement;
    private Etudiant etudiant;

    @InjectMocks
    private UniversiteServiceImpl universiteService;



    @BeforeEach
    void setUp() {
        universite = new Universite();
        universite.setIdUniv(1);
        universite.setNomUniv("ESPRIT");
        universite.setAnneeCreation(2003);
        universite.setBudget(100000.0);
        universite.setDepartements(new HashSet<>());

        departement = new Departement();
        departement.setIdDepart(1);
        departement.setEtudiants(new HashSet<>());

        etudiant = new Etudiant();
        etudiant.setIdEtudiant(1);
    }


    @Test
    void testAddUniversite() {
        when(universiteRepository.save(universite)).thenReturn(universite);

        Universite savedUniversite = universiteService.addUniversite(universite);

        assertNotNull(savedUniversite);
        assertEquals("ESPRIT", savedUniversite.getNomUniv());
        verify(universiteRepository, times(1)).save(universite);
    }

    @Test
    void testRetrieveAllUniversites() {
        when(universiteRepository.findAll()).thenReturn(Arrays.asList(universite));

        List<Universite> universites = universiteService.retrieveAllUniversites();

        assertNotNull(universites);
        assertEquals(1, universites.size());
        verify(universiteRepository, times(1)).findAll();
    }

    @Test
    void testRetrieveUniversite() {
        when(universiteRepository.findById(1)).thenReturn(Optional.of(universite));

        Universite foundUniversite = universiteService.retrieveUniversite(1);

        assertNotNull(foundUniversite);
        assertEquals(1, foundUniversite.getIdUniv());
        verify(universiteRepository, times(1)).findById(1);
    }

    @Test
    void testUpdateUniversite() {
        when(universiteRepository.save(universite)).thenReturn(universite);

        Universite updatedUniversite = universiteService.updateUniversite(universite);

        assertNotNull(updatedUniversite);
        assertEquals(100000.0, updatedUniversite.getBudget());
        verify(universiteRepository, times(1)).save(universite);
    }

    @Test
    void testDeleteUniversite() {
        Universite universite = new Universite();
        when(universiteRepository.findById(anyInt())).thenReturn(Optional.of(universite));
        doNothing().when(universiteRepository).delete(any(Universite.class));

        universiteService.deleteUniversite(1);

        verify(universiteRepository, times(1)).delete(any(Universite.class));
    }
    @Test
    void testRetrieveDepartementsByUniversite() {
        Universite universite = new Universite();
        Departement departement = new Departement();

        universite.setDepartements(new HashSet<>());
        universite.getDepartements().add(departement);

        when(universiteRepository.findById(1)).thenReturn(Optional.of(universite));

        Set<Departement> result = universiteService.retrieveDepartementsByUniversite(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(departement));
    }

    @Test
    void testAttribuerBudgetUniversite() {

        Universite universite = new Universite();
        universite.setAnneeCreation(2000);
        universite.setDepartements(new HashSet<>());

        Departement departement = new Departement();
        departement.setEtudiants(new HashSet<>());

        Etudiant etudiant = new Etudiant();


        departement.getEtudiants().add(etudiant);
        universite.getDepartements().add(departement);

        when(universiteRepository.findById(1)).thenReturn(Optional.of(universite));

        universiteService.attribuerBudgetUniversite(1);

        double expectedBudget = (1 * 100) + (2024 - 2000) * 500;
        assertEquals(expectedBudget, universite.getBudget());

        verify(universiteRepository, times(1)).save(universite);
    }

    @Test
    void testAssignUniversiteToDepartementToEtudiant() {
        when(universiteRepository.findById(1)).thenReturn(Optional.of(universite));
        when(departementRepository.findById(1)).thenReturn(Optional.of(departement));
        when(etudiantRepository.findById(1)).thenReturn(Optional.of(etudiant));

        universiteService.assignUniversiteToDepartementToEtudiant(1, 1, 1);

        assertTrue(universite.getDepartements().contains(departement));
        assertTrue(departement.getEtudiants().contains(etudiant));
        verify(universiteRepository, times(1)).save(universite);
        verify(departementRepository, times(1)).save(departement);
        verify(etudiantRepository, times(1)).save(etudiant);
    }
    @Test
    void testGetUniversitesByBudget() {
        when(universiteRepository.findUniversitesByBudget(5000)).thenReturn(List.of(universite));

        List<Universite> result = universiteService.getUniversitesByBudget(5000);
        assertEquals(1, result.size());
        assertEquals("ESPRIT", result.get(0).getNomUniv());
    }

    @Test
    void testGetUniversitesByAnnee() {
        when(universiteRepository.findUniversitesByAnneeCreation(2003)).thenReturn(List.of(universite));

        List<Universite> result = universiteService.getUniversitesByAnnee(2003);
        assertEquals(1, result.size());
    }

    @Test
    void testGetUniversitesPaged() {
        Page<Universite> page = new PageImpl<>(List.of(universite));
        when(universiteRepository.findAll(PageRequest.of(0, 10, Sort.by("nomUniv")))).thenReturn(page);

        Page<Universite> result = universiteService.getUniversitesPaged(0, 10, "nomUniv");
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetUniversitesByMinEtudiants() {
        when(universiteRepository.findUniversitesByMinEtudiants(100)).thenReturn(List.of(universite));

        List<Universite> result = universiteService.getUniversitesByMinEtudiants(100);
        assertEquals(1, result.size());
    }
}
