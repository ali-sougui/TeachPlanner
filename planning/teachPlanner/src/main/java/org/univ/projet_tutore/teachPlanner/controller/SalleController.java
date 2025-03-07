package org.univ.projet_tutore.teachPlanner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.univ.projet_tutore.teachPlanner.model.Salle;
import org.univ.projet_tutore.teachPlanner.model.TypeSalle;
import org.univ.projet_tutore.teachPlanner.service.SalleService;

import java.util.Arrays;

@Controller
@RequestMapping("/salles")
public class SalleController {

    @Autowired
    private SalleService salleService;

    // Afficher toutes les salles
    @GetMapping
    public String getAllSalles(Model model) {
        model.addAttribute("salles", salleService.getAllSalles());
        return "pages/salles";
    }

    // Afficher le formulaire pour ajouter une salle
    @GetMapping("/nouveau")
    public String showNewSalleForm(Model model) {
        model.addAttribute("salle", new Salle());
        return "pages/nouvelleSalle";
    }

    // Enregistrer une salle
    @PostMapping
    public String saveSalle(@ModelAttribute Salle salle, Model model) {
        try {
            if (salle.getNomSalle() == null || salle.getNomSalle().isEmpty()) {
                model.addAttribute("error", "Le nom de la salle est obligatoire. Veuillez le renseigner.");
                return "pages/nouvelleSalle";
            }
            if (salle.getTypeSalle() == null) {
                model.addAttribute("error", "Le type de la salle est obligatoire. Veuillez sélectionner un type.");
                return "pages/nouvelleSalle";
            }
            salleService.addSalle(salle);
            return "redirect:/salles";
        } catch (Exception e) {
            //e.printStackTrace();
            model.addAttribute("error", "Une erreur inattendue est survenue lors de l'ajout de la salle. Veuillez réessayer plus tard.");
            return "pages/nouvelleSalle";
        }
    }

    /// Méthode pour afficher le formulaire de modification (pas de changement)
    @GetMapping("/modifier/{id}")
    public String modifierSalle(@PathVariable Integer id, Model model) {
        try {
            Salle salle = salleService.getSalleById(id);

            if (salle == null) {
                throw new IllegalArgumentException("Aucune salle trouvée avec l'identifiant : " + id);
            }

            model.addAttribute("salle", salle);
            return "pages/modifierSalle";
        } catch (Exception e) {
          //  e.printStackTrace();
            model.addAttribute("error", "Impossible d'accéder à la salle spécifiée. Veuillez vérifier l'identifiant et réessayer.");
            return "pages/error";
        }
    }
    @PostMapping("/modifier/{id}")
    @ResponseBody
    public String updateSalle(@PathVariable Integer id, @RequestParam String nom, @RequestParam TypeSalle type) {
        System.out.println("Updating salle with id: " + id + ", nom: " + nom + ", type: " + type);
        System.out.println("Type de la variable type : " + type.getClass().getName());

        try {
            Salle salle = salleService.getSalleById(id);
            if (salle != null) {
                salle.setNomSalle(nom);
                salle.setTypeSalle(type);
                salleService.updateSalle(salle);
                return "success";
            }
            return "error: Salle non trouvée";
        } catch (Exception e) {
            //e.printStackTrace();
            return "error: " + e.getMessage();
        }
    }
    // Méthode pour enregistrer les modifications
    @PostMapping("/modifier")
    public String enregistrerModificationSalle(@ModelAttribute("salle") Salle nouvelleSalle, Model model) {
        try {
            // Vérifications des champs obligatoires
            if (nouvelleSalle.getNomSalle() == null || nouvelleSalle.getNomSalle().isEmpty()) {
                model.addAttribute("error", "Le nom de la salle est obligatoire.");
                model.addAttribute("salle", nouvelleSalle);
                return "pages/modifierSalle";
            }
            if (nouvelleSalle.getTypeSalle() == null) {
                model.addAttribute("error", "Le type de la salle est obligatoire.");
                model.addAttribute("salle", nouvelleSalle);
                return "pages/modifierSalle";
            }

            // Mise à jour de la salle
            salleService.updateSalle(nouvelleSalle);
            return "redirect:/salles";
        } catch (Exception e) {
            //e.printStackTrace();
            model.addAttribute("error", "Une erreur inattendue est survenue lors de la modification de la salle. Veuillez réessayer plus tard.");
            model.addAttribute("salle", nouvelleSalle);
            return "pages/modifierSalle";
        }
    }


    // Supprimer une salle
    @PostMapping("/supprimer/{id}")
    public String supprimerSalle(@PathVariable Integer id) {
        try {
            salleService.supprimerSalle(id);
            return "redirect:/salles";
        } catch (Exception e) {
           // e.printStackTrace();
            return "redirect:/salles?error=La suppression de la salle a échoué. Veuillez vérifier si elle est utilisée ailleurs dans le système.";
        }
    }
}
