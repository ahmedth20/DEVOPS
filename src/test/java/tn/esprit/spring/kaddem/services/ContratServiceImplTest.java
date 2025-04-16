// package tn.esprit.spring.kaddem.services;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.extension.ExtendWith;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.Mockito;
// import org.mockito.MockitoAnnotations;
// import org.mockito.junit.jupiter.MockitoExtension;
// import tn.esprit.spring.kaddem.entities.Contrat;
// import tn.esprit.spring.kaddem.entities.Etudiant;
// import tn.esprit.spring.kaddem.entities.Specialite;
// import tn.esprit.spring.kaddem.repositories.ContratRepository;
// import tn.esprit.spring.kaddem.repositories.EtudiantRepository;

// import java.util.*;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;

// @ExtendWith(MockitoExtension.class)
// class ContratServiceImplTest {

//     @Mock
//     private ContratRepository contratRepository;

//     @Mock
//     private EtudiantRepository etudiantRepository;

//     @InjectMocks
//     private ContratServiceImpl contratService;

//     private Contrat contrat;
//     private Etudiant etudiant;

//     @BeforeEach
//     void setUp() {
//         MockitoAnnotations.openMocks(this);
//         contrat = new Contrat();
//         contrat.setIdContrat(1);
//         contrat.setArchive(false);
//         contrat.setSpecialite(Specialite.IA);
//         contrat.setDateFinContrat(new Date());

//         etudiant = new Etudiant();
//         etudiant.setIdEtudiant(1);
//         etudiant.setNomE("John");
//         etudiant.setPrenomE("Doe");
//     }

//     @Test
//     void addContrat() {
//         when(contratRepository.save(contrat)).thenReturn(contrat);
//         Contrat savedContrat = contratService.addContrat(contrat);
//         assertNotNull(savedContrat);
//         assertEquals(1, savedContrat.getIdContrat());
//     }

//     @Test
//     void retrieveAllContrats() {
//         List<Contrat> contrats = Arrays.asList(contrat);
//         when(contratRepository.findAll()).thenReturn(contrats);
//         List<Contrat> retrieved = contratService.retrieveAllContrats();
//         assertFalse(retrieved.isEmpty());
//         assertEquals(1, retrieved.size());
//     }

//     @Test
//     void updateContrat() {
//         when(contratRepository.save(contrat)).thenReturn(contrat);
//         Contrat updated = contratService.updateContrat(contrat);
//         assertNotNull(updated);
//     }

//     @Test
//     void retrieveContrat() {
//         when(contratRepository.findById(1)).thenReturn(Optional.of(contrat));
//         Contrat retrieved = contratService.retrieveContrat(1);
//         assertNotNull(retrieved);
//     }

//     @Test
//     void removeContrat() {
//         when(contratRepository.findById(1)).thenReturn(Optional.of(contrat));
//         doNothing().when(contratRepository).delete(contrat);
//         contratService.removeContrat(1);
//         verify(contratRepository, times(1)).delete(contrat);
//     }

//     @Test
//     void affectContratToEtudiant() {
//         when(etudiantRepository.findByNomEAndPrenomE("John", "Doe")).thenReturn(etudiant);
//         when(contratRepository.findByIdContrat(1)).thenReturn(contrat);
//         contrat.setEtudiant(etudiant);
//         when(contratRepository.save(contrat)).thenReturn(contrat);

//         Contrat affected = contratService.affectContratToEtudiant(1, "John", "Doe");
//         assertNotNull(affected);
//         assertEquals(etudiant, affected.getEtudiant());
//     }

//     @Test
//     void nbContratsValides() {
//         Date startDate = new Date();
//         Date endDate = new Date();
//         when(contratRepository.getnbContratsValides(startDate, endDate)).thenReturn(5);
//         Integer count = contratService.nbContratsValides(startDate, endDate);
//         assertEquals(5, count);
//     }

//     @Test
//     void retrieveAndUpdateStatusContrat() {
//         List<Contrat> contrats = Arrays.asList(contrat);
//         when(contratRepository.findAll()).thenReturn(contrats);
//         contratService.retrieveAndUpdateStatusContrat();
//         verify(contratRepository, times(1)).save(any(Contrat.class));
//     }

//     @Test
//     void getChiffreAffaireEntreDeuxDates() {
//         Date startDate = new Date();
//         Date endDate = new Date(System.currentTimeMillis() + (1000 * 60 * 60 * 24 * 60)); // 2 mois après
//         when(contratRepository.findAll()).thenReturn(Arrays.asList(contrat));

//         float chiffre = contratService.getChiffreAffaireEntreDeuxDates(startDate, endDate);
//         assertTrue(chiffre > 0);
//     }
// }
