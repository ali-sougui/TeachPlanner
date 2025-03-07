package org.univ.projet_tutore.teachPlanner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.univ.projet_tutore.teachPlanner.model.Enseignant;
import org.univ.projet_tutore.teachPlanner.model.Personnel;
import org.univ.projet_tutore.teachPlanner.repository.EnseignantRepository;
import org.univ.projet_tutore.teachPlanner.repository.PersonnelRepository;

import java.util.List;

@Service
public class EnseignantService {

	@Autowired
	private EnseignantRepository enseignantRepository;

	@Autowired
	private PersonnelRepository personnelRepository;

	public List<Enseignant> getAllEnseignants() {
		return enseignantRepository.findAll();
	}

	@Transactional(readOnly = false)
	public Enseignant addEnseignant(Enseignant enseignant) {
		return enseignantRepository.save(enseignant);
	}

	@Transactional
	public void deleteEnseignantById(Long id) {
		enseignantRepository.deleteById(id);
	}

	@Transactional
	public Enseignant updateEnseignant(Enseignant enseignant) {
		return enseignantRepository.save(enseignant); // Met à jour si l'ID existe
	}
	public Enseignant findByPersonnel(Personnel personnel) {
		return enseignantRepository.findByPersonnel(personnel);
	}
	public Enseignant findByPersonnel(Integer idPersonnel) {
		Personnel personnel = personnelRepository.findById(idPersonnel)
				.orElseThrow(() -> new RuntimeException("Personnel non trouvé"));
		return enseignantRepository.findByPersonnel(personnel);
	}

	public Enseignant getEnseignantById(Long id) {
		return enseignantRepository.findById(id).orElse(null);
	}
	public List<Enseignant> searchEnseignants(String searchTerm) {
	    return enseignantRepository.findByNomEnsContainingOrPrenomEnsContaining(searchTerm, searchTerm);
	}
	public Enseignant getEnseignantById(Integer id) {
		return enseignantRepository.findById(Long.valueOf(id))
				.orElseThrow(() -> new RuntimeException("Enseignant non trouvé"));
	}
	@Transactional
	public Enseignant createEnseignant(Enseignant enseignant) {
		// Vérifier si le personnel existe
		if (enseignant.getPersonnel() != null && enseignant.getPersonnel().getIdperso() != null) {
			Personnel personnel = personnelRepository.findById(enseignant.getPersonnel().getIdperso())
					.orElseThrow(() -> new RuntimeException("Personnel non trouvé"));

			// Utiliser l'instance récupérée de la base de données
			enseignant.setPersonnel(personnel);
		}

		// Si l'ID est fourni, vérifier qu'il n'existe pas déjà
		if (enseignant.getNumeroEns() != null) {
			boolean exists = enseignantRepository.existsById(Long.valueOf(enseignant.getNumeroEns()));
			if (exists) {
				throw new RuntimeException("Un enseignant avec ce numéro existe déjà: " + enseignant.getNumeroEns());
			}
		}

		return enseignantRepository.save(enseignant);
	}

	@Transactional
	public Enseignant createEnseignant(String nom, String prenom, Personnel personnel) {
		// Vérifier que le personnel a le rôle ENSEIGNANT
		if (personnel.getRole() != Personnel.Role.ENSEIGNANT) {
			throw new RuntimeException("Le personnel doit avoir le rôle ENSEIGNANT");
		}

		// Vérifier qu'il n'existe pas déjà un enseignant pour ce personnel
		if (enseignantRepository.findByPersonnel(personnel)!=null) {
			throw new RuntimeException("Un enseignant existe déjà pour ce personnel");
		}

		Enseignant enseignant = new Enseignant();
		enseignant.setNomEns(nom);
		enseignant.setPrenomEns(prenom);
		enseignant.setPersonnel(personnel);

		// Assurez-vous que l'ID est null pour permettre l'auto-génération
		enseignant.setNumeroEns(null);

		return enseignantRepository.save(enseignant);
	}

	@Transactional
	public void deleteEnseignant(Integer id) {
		Enseignant enseignant = getEnseignantById(id);
		enseignantRepository.delete(enseignant);
	}
//	public Integer getNumeroEnsByIdPerso(Integer idperso) {
//		try {
//			return enseignantRepository.findByPersonnel(idperso)
//					.map(Enseignant::getNumeroEns)
//					.orElse(null);
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}


}