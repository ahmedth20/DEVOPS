package tn.esprit.spring.kaddem.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import tn.esprit.spring.kaddem.entities.Contrat;
import tn.esprit.spring.kaddem.entities.Equipe;
import tn.esprit.spring.kaddem.entities.Etudiant;
import tn.esprit.spring.kaddem.entities.Niveau;
import tn.esprit.spring.kaddem.repositories.EquipeRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

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
		List<Equipe> equipes = (List<Equipe>) equipeRepository.findAll();

		for (Equipe equipe : equipes) {
			if (equipe.getNiveau().equals(Niveau.JUNIOR) || equipe.getNiveau().equals(Niveau.SENIOR)) {
				List<Etudiant> etudiants = (List<Etudiant>) equipe.getEtudiants();
				int nbEtudiantsAvecContratsActifs = 0;

				for (Etudiant etudiant : etudiants) {
					Set<Contrat> contrats = etudiant.getContrats();
					for (Contrat contrat : contrats) {
						if (contrat.getArchive() == false && isContratActif(contrat)) {
							nbEtudiantsAvecContratsActifs++;
							break; // Pas besoin de vérifier plus de contrats pour cet étudiant
						}
					}
					if (nbEtudiantsAvecContratsActifs >= 3) {
						break; // Si on a trouvé assez d'étudiants avec des contrats actifs
					}
				}

				// Si l'équipe a 3 ou plus d'étudiants avec des contrats actifs, on évolue son niveau
				if (nbEtudiantsAvecContratsActifs >= 3) {
					if (equipe.getNiveau().equals(Niveau.JUNIOR)) {
						equipe.setNiveau(Niveau.SENIOR);
						equipeRepository.save(equipe);
						log.info("Equipe {} promoted to SENIOR", equipe.getNomEquipe());
						break; // On passe à la prochaine équipe après évolution
					}
					if (equipe.getNiveau().equals(Niveau.SENIOR)) {
						equipe.setNiveau(Niveau.EXPERT);
						equipeRepository.save(equipe);
						log.info("Equipe {} promoted to EXPERT", equipe.getNomEquipe());
						break;
					}
				}
			}
		}
	}

	// Méthode utilitaire pour vérifier si un contrat est actif depuis plus d'un an
	private boolean isContratActif(Contrat contrat) {
		LocalDate dateFinContrat = contrat.getDateFinContrat().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
		LocalDate currentDate = LocalDate.now();
		return dateFinContrat.isBefore(currentDate.minusYears(1));
	}
}
