package tn.esprit.spring.kaddem.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.kaddem.entities.Universite;
import tn.esprit.spring.kaddem.repositories.UniversiteRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UniversiteServiceImplMockTest {
    @Mock
    private UniversiteRepository universiteRepository;

    @InjectMocks
    private UniversiteServiceImpl universiteService;

    private Universite universite;

    @BeforeEach
    void setUp() {
        universite = new Universite();
        universite.setIdUniv(1);
        universite.setNomUniv("Esprit");
        universite.setAnneeCreation(2003);
        universite.setBudget(100000.0);
    }

    @Test
    void testAddUniversite() {
        when(universiteRepository.save(universite)).thenReturn(universite);

        Universite savedUniversite = universiteService.addUniversite(universite);

        assertNotNull(savedUniversite);
        assertEquals("Esprit", savedUniversite.getNomUniv());
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

}
