package tn.esprit.spring.kaddem.services;

import tn.esprit.spring.kaddem.entities.Departement;

import java.util.List;
import java.util.Map;

public interface IDepartementService {

    public Departement addDepartement (Departement d);

    public   Departement updateDepartement (Departement d);

    public  Departement retrieveDepartement (Integer idDepart);

    public  void deleteDepartement(Integer idDepartement);


    public Map<String,Long> getNombreEtudiantsParDepartement();

    List<Departement> filterDepartements(String nomDepart, Integer minEtudiants);

    }
