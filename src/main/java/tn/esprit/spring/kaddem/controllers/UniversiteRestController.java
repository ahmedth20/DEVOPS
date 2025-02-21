package tn.esprit.spring.kaddem.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.spring.kaddem.entities.Departement;
import tn.esprit.spring.kaddem.entities.Universite;
import tn.esprit.spring.kaddem.services.IUniversiteService;

import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/universite")
public class UniversiteRestController {
	@Autowired
	IUniversiteService universiteService;
	// http://localhost:8089/Kaddem/universite/retrieve-all-universites
	@GetMapping("/retrieve-all-universites")
	public List<Universite> getUniversites() {
		List<Universite> listUniversites = universiteService.retrieveAllUniversites();
		return listUniversites;
	}
	// http://localhost:8089/Kaddem/universite/retrieve-universite/8
	@GetMapping("/retrieve-universite/{universite-id}")
	public Universite retrieveUniversite(@PathVariable("universite-id") Integer universiteId) {
		return universiteService.retrieveUniversite(universiteId);
	}

	// http://localhost:8089/Kaddem/universite/add-universite
	@PostMapping("/add-universite")
	public Universite addUniversite(@RequestBody Universite u) {
		Universite universite = universiteService.addUniversite(u);
		return universite;
	}

	// http://localhost:8089/Kaddem/universite/remove-universite/1
	@DeleteMapping("/remove-universite/{universite-id}")
	public void removeUniversite(@PathVariable("universite-id") Integer universiteId) {
		universiteService.deleteUniversite(universiteId);
	}

	// http://localhost:8089/Kaddem/universite/update-universite
	@PutMapping("/update-universite")
	public Universite updateUniversite(@RequestBody Universite u) {
		Universite u1= universiteService.updateUniversite(u);
		return u1;
	}

	//@PutMapping("/affecter-etudiant-departement")
	@PutMapping(value="/affecter-universite-departement/{universiteId}/{departementId}")
	public void affectertUniversiteToDepartement(@PathVariable("universiteId") Integer universiteId, @PathVariable("departementId")Integer departementId){
		universiteService.assignUniversiteToDepartement(universiteId, departementId);
	}

	@GetMapping(value = "/listerDepartementsUniversite/{idUniversite}")
	public Set<Departement> listerDepartementsUniversite(@PathVariable("idUniversite") Integer idUniversite) {

		return universiteService.retrieveDepartementsByUniversite(idUniversite);
	}
	@PutMapping("/attribuer-budget/{idUniv}")
	public ResponseEntity<String> attribuerBudget(@PathVariable("idUniv") Integer idUniv) {
		universiteService.attribuerBudgetUniversite(idUniv);
		return ResponseEntity.ok("Budget attribué avec succès à l'université " + idUniv);
	}

	@PutMapping(value="/assignUniversiteToDepartementToEtudiant/{idUniversite}/{idDepartement}/{idEtudiant}")
	public void assignUniversiteToDepartementToEtudiant(Integer idUniversite, Integer idDepartement, Integer idEtudiant) {
      universiteService.assignUniversiteToDepartementToEtudiant(idUniversite, idDepartement, idEtudiant);
	}
	@GetMapping("/budget/{budget}")
	public List<Universite> getUniversitesByBudget(@PathVariable double budget) {
		return universiteService.getUniversitesByBudget(budget);
	}
	@GetMapping("/annee/{annee}")
	public List<Universite> getUniversitesByAnnee(@PathVariable int annee) {
		return universiteService.getUniversitesByAnnee(annee);
	}
	@GetMapping("/paged")
	public Page<Universite> getUniversitesPaged(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "id") String sortBy) {
		return universiteService.getUniversitesPaged(page, size, sortBy);
	}
	@GetMapping("/minEtudiants/{minEtudiants}")
	public List<Universite> getUniversitesByMinEtudiants(@PathVariable long minEtudiants) {
		return universiteService.getUniversitesByMinEtudiants(minEtudiants);
	}

	}


