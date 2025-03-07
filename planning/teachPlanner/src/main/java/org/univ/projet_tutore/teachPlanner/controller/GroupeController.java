package org.univ.projet_tutore.teachPlanner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.univ.projet_tutore.teachPlanner.model.Groupe;
import org.univ.projet_tutore.teachPlanner.service.GroupeService;

import java.util.List;

@Controller  // Changement de @RestController à @Controller
@RequestMapping("api/groupes")
public class GroupeController {

    private final GroupeService groupeService;

    @Autowired
    public GroupeController(GroupeService groupeService) {
        this.groupeService = groupeService;
    }

    // 👉 Cette méthode affiche la page HTML des groupes
    @GetMapping
    public String afficherGroupes(Model model) {
        List<Groupe> groupes = groupeService.getAllGroupes();
        model.addAttribute("groupes", groupes);
        return "pages/groupes"; // Le fichier HTML doit être dans src/main/resources/templates/groupes.html
    }

    // 👉 Cette méthode renvoie les groupes en JSON pour l'AJAX
    @GetMapping("/api")
    @ResponseBody
    public List<Groupe> getAllGroupes() {
        return groupeService.getAllGroupes();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public Groupe getGroupe(@PathVariable Integer id) {
        return groupeService.getGroupeById(id);
    }


    // ✅ EndPoint pour créer un nouveau groupe
    @PostMapping
    public ResponseEntity<?> createGroupe(@RequestBody Groupe groupe) {
        try {
            groupe.setId(null); // ✅ Assurez-vous que l'ID est nul pour l’auto-génération
            Groupe savedGroupe = groupeService.createGroupe(groupe);
            return ResponseEntity.ok(savedGroupe);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la création du groupe : " + e.getMessage());
        }
    }



    @DeleteMapping("/{id}")
    @ResponseBody
    public void deleteGroupe(@PathVariable Integer id) {
        groupeService.deleteGroupe(id);
    }
}
