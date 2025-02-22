package org.univ.projet_tutore.teachPlanner.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.univ.projet_tutore.teachPlanner.model.Enseignant;
import org.univ.projet_tutore.teachPlanner.service.EnseignantService;



@Controller
@RequestMapping("/enseignants")
public class EnseignantController {
    @Autowired
    private EnseignantService enseignantService;

    @GetMapping("/all")
    public String getAllEnseignants(@RequestParam(value = "search", required = false) String searchTerm, Model model) {
        List<Enseignant> enseignants;
        
        // Si un terme de recherche est donné, on filtre les enseignants
        if (searchTerm != null && !searchTerm.isEmpty()) {
            enseignants = enseignantService.searchEnseignants(searchTerm); // Méthode de recherche à ajouter dans le service
        } else {
            enseignants = enseignantService.getAllEnseignants();
        }
        
        model.addAttribute("enseignants", enseignants);
        model.addAttribute("searchTerm", searchTerm); // Pour garder le terme de recherche dans le champ
        return "pages/enseignant";
    }

    
    // Route pour afficher le formulaire d'ajout
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("newEnseignant", new Enseignant());
        return "pages/ajoutEnseignant";  // Nom du template de la page d'ajout
    }

    @PostMapping("/add")
    public String addEnseignant(@ModelAttribute("newEnseignant") Enseignant enseignant, Model model) {
        enseignantService.addEnseignant(enseignant);
        return "redirect:/enseignants/all";
    }

    @PostMapping("/{id}")
    public String deleteEnseignant(@PathVariable Long id, Model model) {
        enseignantService.deleteEnseignantById(id); 
        return "redirect:/enseignants/all";
    }
    

    @PostMapping("/update/{id}")
    public String updateEnseignant(@PathVariable Long id, @ModelAttribute Enseignant enseignant, Model model) {
        Enseignant existingEnseignant = enseignantService.getEnseignantById(id);
        if (existingEnseignant != null) {
        	System.out.println("Requête pour la mise à jour de l'enseignant avec ID : " + id);
            existingEnseignant.setNom(enseignant.getNom());
            existingEnseignant.setPrenom(enseignant.getPrenom());
            existingEnseignant.setIdperso(enseignant.getIdperso());
            enseignantService.updateEnseignant(existingEnseignant);
        }
        return "redirect:/enseignants/all";
    }
}
