package tn.esprit.spring.kaddem.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.entities.Universite;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;
import tn.esprit.spring.kaddem.repositories.UniversiteRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class UniversiteServiceImpl implements IUniversiteService {
    @Autowired
    UniversiteRepository universiteRepository;
    @Autowired
    DepartementRepository departementRepository;
    @Autowired
    EtudiantRepository etudiantRepository;

    public UniversiteServiceImpl() {
        // TODO Auto-generated constructor stub
    }

    public List<Universite> retrieveAllUniversites() {
        return (List<Universite>) universiteRepository.findAll();
    }

    public Universite addUniversite(Universite u) {
        return (universiteRepository.save(u));
    }

    public Universite updateUniversite(Universite u) {
        return (universiteRepository.save(u));
    }

    @Override
    public Universite retrieveUniversite(Integer idUniversite) {
        return universiteRepository.findById(idUniversite)
                .orElseThrow(() -> new NoSuchElementException("Université avec l'ID " + idUniversite + " non trouvée"));

    }

    @Override
    public void deleteUniversite(Integer idUniversite) {
        Universite universite = retrieveUniversite(idUniversite); // Vérifie si l'université existe
        universiteRepository.delete(universite);

    }

    public void assignUniversiteToDepartement(Integer idUniversite, Integer idDepartement) {
        Universite u = universiteRepository.findById(idUniversite).orElse(null);
        Departement d = departementRepository.findById(idDepartement).orElse(null);
        u.getDepartements().add(d);
        universiteRepository.save(u);
    }

    public Set<Departement> retrieveDepartementsByUniversite(Integer idUniversite) {
        Universite u = universiteRepository.findById(idUniversite).orElse(null);
        return u.getDepartements();
    }

    @Override
    public void attribuerBudgetUniversite(Integer idUniv) {
        Universite universite = universiteRepository.findById(idUniv)
                .orElseThrow(() -> new RuntimeException("Université non trouvée"));
        Set<Departement> departements = universite.getDepartements();
        if (departements == null || departements.isEmpty()) {
            throw new RuntimeException("Aucun département trouvé pour cette université");
        }
        double totalBudget = 0.0F;
        for (Departement d : departements) {
            Set<Etudiant> etudiants = d.getEtudiants();
            int nombreEtudiants = (etudiants != null) ? etudiants.size() : 0;

            int anciennete = (universite.getAnneeCreation() != null) ? (2024 - universite.getAnneeCreation()) : 0;

            totalBudget += (nombreEtudiants * 100) + (anciennete * 500);
        }
        universite.setBudget(totalBudget);
        universiteRepository.save(universite);
    }

    @Override
    public void assignUniversiteToDepartementToEtudiant(Integer idUniversite, Integer idDepartement, Integer idEtudiant) {
        Universite universite = universiteRepository.findById(idUniversite)
                .orElseThrow(() -> new RuntimeException("Université non trouvée"));
        Departement departement = departementRepository.findById(idDepartement)
                .orElseThrow(() -> new RuntimeException("Département non trouvé"));
        Etudiant etudiant = etudiantRepository.findById(idEtudiant)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        // Vérifier si l'étudiant appartient déjà à un département différent
        if (etudiant.getDepartement() != null && !etudiant.getDepartement().equals(departement)) {
            throw new RuntimeException("L'étudiant est déjà assigné à un autre département.");
        }

        // Ajouter le département à l'université s'il n'est pas déjà présent
        if (!universite.getDepartements().contains(departement)) {
            universite.getDepartements().add(departement);
        }

        // Ajouter l'étudiant au département s'il n'est pas déjà dedans
        if (!departement.getEtudiants().contains(etudiant)) {
            departement.getEtudiants().add(etudiant);
            etudiant.setDepartement(departement);
        }

        universiteRepository.save(universite);
        departementRepository.save(departement);
        etudiantRepository.save(etudiant);
    }

    @Override
    public List<Universite> getUniversitesByBudget(double budget) {
        return universiteRepository.findUniversitesByBudget(budget);
    }

    @Override
    public List<Universite> getUniversitesByAnnee(int annee) {
        return universiteRepository.findUniversitesByAnneeCreation(annee);
    }

    @Override
    public Page<Universite> getUniversitesPaged(int page, int size, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return universiteRepository.findAll(pageable);

    }

    @Override
    public List<Universite> getUniversitesByMinEtudiants(long minEtudiants) {
        return universiteRepository.findUniversitesByMinEtudiants(minEtudiants);

    }


}

