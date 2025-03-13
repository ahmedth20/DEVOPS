package tn.esprit.spring.kaddem.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.repositories.DepartementRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class DepartementServiceImpl implements IDepartementService {

	@Autowired
	DepartementRepository departementRepository;

	@Override
	public Departement addDepartement(Departement d) {
		return departementRepository.save(d);
	}

	@Override
	public Departement updateDepartement(Departement d) {
		return departementRepository.save(d);
	}

	@Override
	public Departement retrieveDepartement(Integer idDepart) {
		return departementRepository.findById(idDepart).orElseThrow(() -> new RuntimeException("Departement not found"));
	}

	@Override
	public void deleteDepartement(Integer idDepartement) {
		Departement d = retrieveDepartement(idDepartement);
		departementRepository.delete(d);
	}




	@Override
	public Map<String, Long> getNombreEtudiantsParDepartement() {
		List<Departement> departements = departementRepository.findAll();
		Map<String, Long> stats = new HashMap<>();
		for (Departement departement : departements) {
			long count = departement.getEtudiants().size();
			stats.put(departement.getNomDepart(), count);
		}
		return stats;
	}

	@Override
	public List<Departement> filterDepartements(String nomDepart, Integer minEtudiants) {
		return departementRepository.filterDepartements(nomDepart, minEtudiants);
	}
}
