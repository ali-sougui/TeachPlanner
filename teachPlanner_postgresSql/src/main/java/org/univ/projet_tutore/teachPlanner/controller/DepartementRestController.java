package org.univ.projet_tutore.teachPlanner.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.univ.projet_tutore.teachPlanner.model.Departement;
import org.univ.projet_tutore.teachPlanner.service.DepartementService;

import jakarta.validation.Valid;

/*
 * Le Controller est responsable des requêtes de gérer les requêtes HTTP entrantes
 * d'appeler les services appropriés pour la logique métier,
 * et de renvoyer les réponses au client.
 *
 *
 * L'annotation @RequestMapping permet de définir un chemin de base
 * pour toutes les requêtes de ce contrôleur.
 *
 * pour ce faire , on ajoute un mapping comme ("/nomTable")
 * pour indiquer que toutes les requêtes liées aux départements passeront par ce contrôleur.
 *
 *
 * Le contrôleur utilise le service pour exécuter la logique métier.
 * Cela suit le principe de séparation des responsabilités.*
 *
 *
 * Chaque méthode du contrôleur correspond à une action spécifique
 * (récuperer, ajouter , mettre à jour  ou supprimer des départements).
 *
*/



@RestController
@RequestMapping("/api/departements")
public class DepartementRestController {

	//injection de Service.
	@Autowired
	private DepartementService service;

//recuperer tous les départements
	@GetMapping
	public List<Departement> getAllDepartment(){
		return service.getAllDepartment();
	}

//recuperer un departement à l'aide de son id.
	@GetMapping("/{id}")
	public ResponseEntity<Departement> getByIdDepartment(@PathVariable Integer id) {
		Departement departement = service.getByIdDepartment(id);
		return ResponseEntity.ok(departement);
	}

//crée un nouveau département si il y en a pas
	@PostMapping
	public ResponseEntity<Departement> addDepartment(@RequestBody @Valid Departement departement){
		Departement newDepartement = service.addOrUpdateDepartment(departement);
		return ResponseEntity.status(HttpStatus.CREATED).body(newDepartement);
	}

//met à jour un département .
	@PutMapping("/{id}")
	public ResponseEntity<Departement> updateDepartment(
			@PathVariable Integer id,
			@RequestBody @Valid  Departement departement){

		//verifie si le département existe
		//Departement existingDepartment = service.getByIdDepartment(id);

		//Met à jour les champs nécessaires.
		//departement.setNomDepartement(departement.getNomDepartement().toUpperCase());

		// Vérifie si le departement avec cet identifiant existe
		if(!service.existsById(id)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}

		//Récupère le département existant
		//Departement existingDepartment = service.getByIdDepartment(id);

		// Ne met uniquement que le nom du département à jour.
		departement.setIdDepartement(id);
		Departement updatedDepartment = service.addOrUpdateDepartment(departement);

		// Vérifie si le nouvel ID existe déjà (sauf si c'est le même que l'ancien)
		//if (!id.equals(departement.getIdDepartement()) && service.existsById(departement.getIdDepartement())) {
		//	return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
		//}

		// Supprimer l'ancien enregistrement s'il y a un changement d'ID
		//if (!id.equals(departement.getIdDepartement())) {
			//service.deleteById(id);
		//}

		// Enregistrer le département avec le nouvel ID et/ou Nom
		//departement.setIdDepartement(departement.getIdDepartement());
		//Departement updatedDepartment = service.addOrUpdateDepartment(departement);
		return ResponseEntity.ok(updatedDepartment);
	}

//supprimer  un département.
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteById(@PathVariable Integer id) {

		//vérifie si le département existe
		if(!service.existsById(id)) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}

		//Supprime le département
		 service.deleteByIdDepartement(id);
		 return ResponseEntity.noContent().build();
	}

	//afficher la page HTML de la classe Départements
	// Afficher la page HTML pour la gestion des départements
	/*
	@GetMapping("/view")
	public String viewDepartments() {
		return "departements"; // Correspond au fichier departements.html dans templates/
	}

	 */
}

