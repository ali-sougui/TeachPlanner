package org.univ.projet_tutore.teachPlanner.controller;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.univ.projet_tutore.teachPlanner.model.Enseignant;
import org.univ.projet_tutore.teachPlanner.service.EnseignantService;

import java.util.List;

//@RestController
//@RequestMapping("/enseignants")

public class EnseignantControllerAffichageJson {

//    @Autowired
//    private EnseignantService enseignantService;
//
//
//    @GetMapping
//    public List<Enseignant> getAllEnseignants() {
//        List<Enseignant> enseignants = enseignantService.getAllEnseignants();
//        System.out.println("Nombre d'enseignants récupérés : " + enseignants.size());
//        return enseignants;
//    }
//
//
//    @PostMapping
//    public Enseignant addEnseignant(@RequestBody Enseignant enseignant) {
//        enseignantService.addEnseignant(enseignant);
//        return enseignant;
//}

}