package tn.esprit.spring.kaddem.services;

import org.springframework.data.domain.Page;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Universite;

import java.util.List;
import java.util.Set;

public interface IUniversiteService {
   public List<Universite> retrieveAllUniversites();

    Universite addUniversite (Universite  u);

    Universite updateUniversite (Universite  u);

    Universite retrieveUniversite (Integer idUniversite);

    public  void deleteUniversite(Integer idUniversite);

    public void assignUniversiteToDepartement(Integer idUniversite, Integer idDepartement);

    public Set<Departement> retrieveDepartementsByUniversite(Integer idUniversite);

 public void attribuerBudgetUniversite(Integer idUniv);
 public void assignUniversiteToDepartementToEtudiant(Integer idUniversite, Integer idDepartement, Integer idEtudiant) ;

 public List<Universite> getUniversitesByBudget(double budget) ;

 public List<Universite> getUniversitesByAnnee(int annee);

 public Page<Universite> getUniversitesPaged(int page, int size, String sortBy);
 public List<Universite> getUniversitesByMinEtudiants(long minEtudiants);

}
