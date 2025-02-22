package org.univ.projet_tutore.teachPlanner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.univ.projet_tutore.teachPlanner.model.Personnel;
import org.univ.projet_tutore.teachPlanner.service.PersonnelService;

import java.util.List;

@Controller
@RequestMapping("/personnels")
public class PersonnelController {

    @Autowired
    private PersonnelService personnelService;

    // ✅ Méthode pour afficher la liste des personnels
    @GetMapping("/liste")
    public String getAllPersonnels(Model model) {
        List<Personnel> personnels = personnelService.getAllPersonnels();
        model.addAttribute("personnels", personnels); // Ajouter les personnels au modèle
        return "pages/gestion_personnels"; // Retourner le fichier personnels.html
    }
    // ✅ Méthode pour ajouter un nouveau personnel
    @PostMapping
    @ResponseBody
    public Personnel addPersonnel(@RequestBody Personnel personnel) {
        return personnelService.addPersonnel(personnel);
    }
    @GetMapping("/nouveau")
    public String afficherFormulaireAjout() {
        return "pages/personnels"; // Nom du fichier HTML contenant le formulaire
    }
    // ✅ Méthode pour afficher un personnel spécifique via son ID (pour modifier)
    @GetMapping("/modifier/{id}")
    public String getPersonnelToEdit(@PathVariable Integer id, Model model) {
        Personnel personnel = personnelService.getPersonnelById(id);
        model.addAttribute("personnel", personnel);
        return "pages/modifierPersonnel"; // Vue pour modifier un personnel
    }

    // ✅ Méthode pour modifier un personnel via un formulaire
    @PostMapping("/modifier/{id}")
    public String updatePersonnel(@PathVariable Integer id, @ModelAttribute Personnel personnel) {
        personnelService.updatePersonnel(id, personnel);
        return "redirect:/personnels/liste"; // Rediriger vers la liste des personnels
    }

    // ✅ Méthode pour supprimer un personnel
    @PostMapping("/supprimer/{id}")
    public String deletePersonnel(@PathVariable Integer id) {
        personnelService.deletePersonnel(id);
        return "redirect:/personnels/liste"; // Rediriger vers la liste après la suppression
    }
}
