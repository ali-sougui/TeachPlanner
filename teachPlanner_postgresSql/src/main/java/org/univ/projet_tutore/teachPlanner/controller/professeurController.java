package org.univ.projet_tutore.teachPlanner.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;



@Controller
@CrossOrigin(origins = "*")
public class professeurController {
    @GetMapping("/pageProf")
    public String getAccueilProfesseurPage() {
        return "pages/accueilProfesseurs";  // Va chercher le fichier accueilProfesseur.html dans templates/
    }

}

