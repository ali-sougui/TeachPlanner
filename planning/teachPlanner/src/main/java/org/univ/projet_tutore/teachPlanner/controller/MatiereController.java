package org.univ.projet_tutore.teachPlanner.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.univ.projet_tutore.teachPlanner.model.Matiere;
import org.univ.projet_tutore.teachPlanner.service.MatiereService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/matieres")
public class MatiereController {
	
	//injection de service.
	@Autowired 
	private MatiereService service;
	
	@GetMapping("/view")
	public String viewMatieres() {

		return "/pages/matieres";
	}

	@GetMapping("/add")
	public String addMatiereForm() {
		return "/pages/addMatieres";
	}

	@PostMapping("/add")
	public String saveMatieres(@RequestParam("codeMatiere") List<Integer> codeMatieres,
							   @RequestParam("nomMatiere") List<String> nomMatieres) {
		System.out.println("Nombre de matières reçues : " + codeMatieres.size());

		for (int i = 0; i < codeMatieres.size(); i++) {
			System.out.println("Matière reçue : " + codeMatieres.get(i) + " - " + nomMatieres.get(i));

			Matiere matiere = new Matiere();
			matiere.setCodeMatiere(codeMatieres.get(i));
			matiere.setNomMatiere(nomMatieres.get(i));

			service.addOrUpdateMatiere(matiere);
		}
		return "redirect:/matieres/view";
	}


}