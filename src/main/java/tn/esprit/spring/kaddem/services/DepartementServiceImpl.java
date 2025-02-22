package tn.esprit.spring.kaddem.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Equipe;
import tn.esprit.spring.kaddem.repositories.ContratRepository;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;

import java.util.List;

@Slf4j

@Service
public class DepartementServiceImpl implements IDepartementService{
	@Autowired
	DepartementRepository departementRepository;


	public Departement addDepartement (Departement d){
		return departementRepository.save(d);
	}

	public   Departement updateDepartement (Departement d){
		return departementRepository.save(d);
	}

	public  Departement retrieveDepartement (Integer idDepart){
		return departementRepository.findById(idDepart).get();
	}
	public  void deleteDepartement(Integer idDepartement){
		Departement d=retrieveDepartement(idDepartement);
		departementRepository.delete(d);
	}

	public String getDepartementStatus(Integer idDepartement) {
		Departement departement = departementRepository.findById(idDepartement).orElse(null);

		if (departement == null) {
			return "Département introuvable";
		}

		int nombreEtudiants = departement.getEtudiants().size();

		if (nombreEtudiants < 5) {
			return "Département dépeuplé";
		} else {
			return "Département normal";
		}
	}




}
