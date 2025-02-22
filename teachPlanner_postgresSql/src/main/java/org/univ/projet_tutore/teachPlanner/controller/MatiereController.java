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
import org.univ.projet_tutore.teachPlanner.model.Matiere;
import org.univ.projet_tutore.teachPlanner.service.MatiereService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/matieres")
public class MatiereController {
	
	//injection de service.
	@Autowired 
	private MatiereService service;
	
	@GetMapping
	public ResponseEntity<List<Matiere>> getAllMatieres(){
		List<Matiere> matieres =  service.getAllMatiere();
		
		return ResponseEntity.ok(matieres);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Matiere> getByIdMatiere(@PathVariable Integer id){
		Matiere matiere = service.getByIdMatiere(id);
		if(matiere == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		return ResponseEntity.ok(matiere);
	}
	
	//Crée une nouvelle matière si il y en a pas.
	@PostMapping
	public ResponseEntity<Matiere> addMatiere(@RequestBody @Valid Matiere matiere){
		Matiere newMatiere = service.updateOrCreateMatiere(matiere);
		return ResponseEntity.status(HttpStatus.CREATED).body(newMatiere);
	}
	
	//met à jour une matière.
	@PutMapping("/{id}")
	public ResponseEntity<Matiere> updateMatiere(
		@PathVariable Integer id,
		@RequestBody @Valid Matiere matiere){
		
		matiere.setCodeMatiere(id);
		
		Matiere updatedMatiere = service.updateOrCreateMatiere(matiere);
		return ResponseEntity.ok(updatedMatiere);
		
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteByIdMatiere(@PathVariable Integer id) {
		try {
			 service.deleteById(id); // Appel au service pour supprimer la matière
		     return ResponseEntity.noContent().build(); // Retourne 204 No Content si tout s'est bien passé
		}catch (RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // Retourne 404 si une exception est levée
		}

	}
	
	@DeleteMapping
	public ResponseEntity<String> deleteAllMatieres() {
		String result = service.deleteAllMatiere();
		if(result.contains("Aucune matière")) {
			 // Si la base est vide, retourner 204 No Content
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
		}
		//Sinon retourner 200,ok toutes les matière supprimées	.
		return ResponseEntity.ok(result);
	}

}
