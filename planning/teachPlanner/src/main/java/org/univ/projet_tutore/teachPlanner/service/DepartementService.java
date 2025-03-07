package org.univ.projet_tutore.teachPlanner.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.univ.projet_tutore.teachPlanner.model.Departement;
import org.univ.projet_tutore.teachPlanner.repository.DepartementRepository;


/*
 * L'annotation @Service indique à Spring que cette classe
 * est un composant de service et qu'elle doit 
 * être gérer par le conteneur Spring(injection des dépendances,etc...)
 * 
*/


/*
 * Les services interargissent avec la base de données via 
 * des interfaces de dépôt(repository).
 * Cela permet de séparer la logique métier(dans le service)
 * de la logique d'accès aux données(dans le repository).
 * 
*/


/*
 * Service gère les actions que le Contrôleur demandera,
 * comme créer un département,
 * afficher la liste des differents départements
 * ou supprimer un département.
 * 
*/
@Service
public class DepartementService {
	//Declarer attribut de type DepartementRepository.
	//Utiliser l'annotation @Autowired pour permettre l'injonction des dépendances.
	
	@Autowired
	private DepartementRepository departementRepository;
	
	public List<Departement> getAllDepartment(){
		
		//findAll(): récupére tous les départements
		return departementRepository.findAll();
	}
	
	public Departement getByIdDepartment(final Integer id) {
		//findById(): récupère un département à l'aide de son id.
		Optional<Departement> optionalDepartement =departementRepository.findById(id);
			if(optionalDepartement.isPresent()) {
				return optionalDepartement.get();
			}
			else {
				throw new RuntimeException("Département nom trouvé pour l'ID : "+ id);
			}
		
	}
	
	public Departement addOrUpdateDepartment(Departement departement) {
		//save():créer ou mettre à jour un département.
		//Departement savedDepartement;
		
		//Règle de gestion : le nom du departement doit être en Majuscule.
		//departement.setNomDepartement(departement.getNomDepartement().toUpperCase());
		
		/*if(departement.getIdDepartement()==null) {
			savedDepartement = departementRepository.createDepartement(departement);
		}
		else {
			savedDepartement = departementRepository.updateDepartement(departement);
			
		}*/
		// Vérifie si l'ID existe déjà
		if (departementRepository.existsById(departement.getIdDepartement())) {
			// Si l'ID existe, on considère qu'il s'agit d'une mise à jour
			Departement existingDepartement = departementRepository.findById(departement.getIdDepartement()).orElse(null);
			if (existingDepartement != null) {
				// Mise à jour des champs nécessaires
				existingDepartement.setNomDepartement(departement.getNomDepartement());
				return departementRepository.save(existingDepartement);
			}
		} else {
			// Si l'ID n'existe pas, c'est une création
			return departementRepository.save(departement);
		}

		throw new IllegalArgumentException("Une erreur inattendue s'est produite.");

	}

	public String deleteAllDepartment() {
		long count = departementRepository.count();
		if(count == 0) {
			return  "Les "+count+" départements ont été supprimés.";
		}
		departementRepository.deleteAll();
		return "Aucun département àsupprimer , la Base de Données est déjà vide";
	}
	
	public void deleteByIdDepartement(final Integer id) {
		departementRepository.deleteById(id);
	}

	public boolean existsById(Integer id) {
		return departementRepository.existsById(id);
	}

	public void deleteById(Integer id) {
		departementRepository.deleteById(id);
	}

}
