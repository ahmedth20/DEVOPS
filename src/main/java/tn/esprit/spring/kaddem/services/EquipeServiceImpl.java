package tn.esprit.spring.kaddem.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import tn.esprit.spring.kaddem.entities.Contrat;
import tn.esprit.spring.kaddem.entities.Equipe;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.entities.Niveau;
import tn.esprit.spring.kaddem.repositories.EquipeRepository;
import tn.esprit.spring.kaddem.repositories.EtudiantRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@AllArgsConstructor
@Service
public class EquipeServiceImpl implements IEquipeService {
	private final EquipeRepository equipeRepository;


	public List<Equipe> retrieveAllEquipes() {
		return (List<Equipe>) equipeRepository.findAll();
	}

	public Equipe addEquipe(Equipe e) {
		return equipeRepository.save(e);
	}

	public void deleteEquipe(Integer idEquipe) {
		Equipe e = retrieveEquipe(idEquipe);
		if (e != null) {
			equipeRepository.delete(e);
		} else {
			log.error("Equipe with id {} not found", idEquipe);
		}
	}

	public Equipe retrieveEquipe(Integer equipeId) {
		return equipeRepository.findById(equipeId).orElse(null);
	}

	public Equipe updateEquipe(Equipe e) {
		return equipeRepository.save(e);
	}

	public void evoluerEquipes() {
		// Récupérer toutes les équipes depuis le repository
		List<Equipe> equipes = StreamSupport.stream(equipeRepository.findAll().spliterator(), false)
				.collect(Collectors.toList());

		for (Equipe equipe : equipes) {
			if (equipe.getNiveau().equals(Niveau.JUNIOR) || equipe.getNiveau().equals(Niveau.SENIOR)) {
				Set<Etudiant> etudiants = equipe.getEtudiants();
				int nbEtudiantsAvecContratsActifs = 0;

				for (Etudiant etudiant : etudiants) {
					Set<Contrat> contrats = etudiant.getContrats();
					for (Contrat contrat : contrats) {
						// Vérifier si le contrat est actif et non archivé
						if (!contrat.getArchive() && isContratActif(contrat)) {
							nbEtudiantsAvecContratsActifs++;
							break; // Pas besoin de vérifier plus de contrats pour cet étudiant
						}
					}

					// Si on a trouvé 3 étudiants avec des contrats actifs, on arrête
					if (nbEtudiantsAvecContratsActifs >= 3) {
						break;
					}
				}

				// Si l'équipe a 3 ou plus d'étudiants avec des contrats actifs, on évolue son niveau
				if (nbEtudiantsAvecContratsActifs >= 3) {
					if (equipe.getNiveau().equals(Niveau.JUNIOR)) {
						equipe.setNiveau(Niveau.SENIOR);
						equipeRepository.save(equipe); // Sauvegarder l'équipe après l'évolution
						log.info("Equipe {} promoted to SENIOR", equipe.getNomEquipe());
					}
					if (equipe.getNiveau().equals(Niveau.SENIOR)) {
						equipe.setNiveau(Niveau.EXPERT);
						equipeRepository.save(equipe);
						log.info("Equipe {} promoted to EXPERT", equipe.getNomEquipe());
					}
				}
			}
		}
	}




	// Méthode utilitaire pour vérifier si un contrat est actif depuis plus d'un an
	// Method to check if a contract is active
	private boolean isContratActif(Contrat contrat) {
		java.util.Date dateFinContrat = contrat.getDateFinContrat();
		LocalDate localDateFinContrat = new java.sql.Date(dateFinContrat.getTime()).toLocalDate();

		LocalDate currentDate = LocalDate.now();
		return localDateFinContrat.isAfter(currentDate.minusYears(1));
	}

	// Récupérer toutes les équipes ayant au moins un étudiant avec un contrat actif
	public List<Equipe> retrieveEquipesWithActiveContrats() {
		List<Equipe> equipes = (List<Equipe>) equipeRepository.findAll();
		List<Equipe> equipesAvecContratsActifs = new ArrayList<>();
		for (Equipe equipe : equipes) {
			for (Etudiant etudiant : equipe.getEtudiants()) {
				for (Contrat contrat : etudiant.getContrats()) {
					if (!contrat.getArchive() && isContratActif(contrat)) {
						equipesAvecContratsActifs.add(equipe);
						break;
					}
				}
			}
		}
		return equipesAvecContratsActifs;
	}
//Récupérer toutes les équipes qui ont un nombre d'étudiants supérieur ou égal à un seuil donné.
	public List<Equipe> retrieveEquipesByMinStudents(int minStudents) {
		List<Equipe> equipes = (List<Equipe>) equipeRepository.findAll();
		List<Equipe> equipesFiltrées = new ArrayList<>();
		for (Equipe equipe : equipes) {
			if (equipe.getEtudiants().size() >= minStudents) {
				equipesFiltrées.add(equipe);
			}
		}
		return equipesFiltrées;
	}

//Compter le nombre de contrats actifs dans une équipe.
	public long countActiveContractsInEquipe(Integer equipeId) {
		Equipe equipe = equipeRepository.findById(equipeId).orElse(null);
		if (equipe != null) {
			return equipe.getEtudiants().stream()
					.flatMap(etudiant -> etudiant.getContrats().stream())
					.filter(contrat -> !contrat.getArchive() && isContratActif(contrat))
					.count();
		}
		return 0;
	}
//Vérifier si l'une des équipes a un contrat expiré.
public boolean hasExpiredContracts(Integer equipeId) {
	Equipe equipe = equipeRepository.findById(equipeId).orElse(null);
	if (equipe != null) {
		return equipe.getEtudiants().stream()
				.flatMap(etudiant -> etudiant.getContrats().stream())
				.anyMatch(contrat -> {
					Date dateFin = contrat.getDateFinContrat();
					Calendar cal = Calendar.getInstance();
					cal.setTime(dateFin);
					LocalDate dateFinContrat = LocalDate.of(
							cal.get(Calendar.YEAR),
							cal.get(Calendar.MONTH) + 1, // Calendar.MONTH est basé sur 0 (Janvier = 0)
							cal.get(Calendar.DAY_OF_MONTH)
					);
					return dateFinContrat.isBefore(LocalDate.now()) && !contrat.getArchive();
				});
	}
	return false;
}
//Récupérer les équipes qui n'ont pas encore d'étudiants assignés.
	public List<Equipe> retrieveEquipesWithoutStudents() {
		List<Equipe> equipes = (List<Equipe>) equipeRepository.findAll();
		List<Equipe> equipesSansEtudiants = new ArrayList<>();
		for (Equipe equipe : equipes) {
			if (equipe.getEtudiants().isEmpty()) {
				equipesSansEtudiants.add(equipe);
			}
		}
		return equipesSansEtudiants;
	}

	//Récupérer les équipes dont le niveau est supérieur à un certain seuil.
	public List<Equipe> retrieveEquipesByMinLevel(Niveau minLevel) {
		List<Equipe> equipes = (List<Equipe>) equipeRepository.findAll();
		List<Equipe> equipesFiltres = new ArrayList<>();
		for (Equipe equipe : equipes) {
			if (equipe.getNiveau().compareTo(minLevel) > 0) {
				equipesFiltres.add(equipe);
			}
		}
		return equipesFiltres;
	}









}
