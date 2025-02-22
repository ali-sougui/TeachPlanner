package org.univ.projet_tutore.teachPlanner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.univ.projet_tutore.teachPlanner.model.Groupe;
import org.univ.projet_tutore.teachPlanner.service.GroupeService;

@Controller
@RequestMapping("/groupes")
public class GroupeController {
    @Autowired
    private GroupeService groupeService;

    // Afficher tous les groupes
    @GetMapping
    public String getAllGroupes(Model model) {
        model.addAttribute("groupes", groupeService.getAllGroupes());
        return "pages/groupes";
    }

    // Afficher le formulaire pour ajouter un nouveau groupe
    @GetMapping("/nouveau")
    public String showNewGroupeForm(Model model) {
        model.addAttribute("groupe", new Groupe());
        return "pages/nouvelleGroupe";
    }


    // Enregistrer un nouveau groupe
    @PostMapping
    public String saveGroupe(@ModelAttribute Groupe groupe, Model model) {
        try {
            if (groupe.getNom_groupe() == null || groupe.getNom_groupe().isEmpty() ||
                    groupe.getType_cours() == null || groupe.getCodeMatiere() == null) {
                System.out.println("Nom Groupe: " + groupe.getNom_groupe());
                System.out.println("Type Cours: " + groupe.getType_cours());
                System.out.println("Code Matiere: " + groupe.getCodeMatiere());

                model.addAttribute("error", "Tous les champs doivent être remplis.");
                return "pages/nouvelleGroupe";
            }
            System.out.println("Type de cours : " + groupe.getType_cours());
            groupeService.addGroupe(groupe);
            return "redirect:/groupes";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Une erreur est survenue lors de l'ajout du groupe.");
            return "pages/nouvelleGroupe";
        }
    }

    // Supprimer un groupe
    @PostMapping("/supprimer/{id}")
    public String supprimerGroupe(@PathVariable Integer id) {
        try {
            groupeService.supprimerGroupe(id);
            return "redirect:/groupes";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/groupes?error=Suppression échouée";
        }
    }
}
