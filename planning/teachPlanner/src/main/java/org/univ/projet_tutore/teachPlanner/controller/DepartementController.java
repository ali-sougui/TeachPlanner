package org.univ.projet_tutore.teachPlanner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.univ.projet_tutore.teachPlanner.model.Departement;
import org.univ.projet_tutore.teachPlanner.service.DepartementService;

import java.util.List;

@Controller
@RequestMapping("/departements")
public class DepartementController {

    @Autowired
    private DepartementService service;

    // Afficher la liste des départements

    @GetMapping("/view")
    public String viewDepartments() {
        return "/pages/departements"; // Affiche src/main/resources/templates/departements.html
    }

    @GetMapping("/add")
    public String addDepartmentForm() {
        return "/pages/addDepartement" ; // charge la page addDepartment.html -
    }

    // Enregistrer plusieurs départements
    @PostMapping("/add")
    public String saveDepartments(@RequestParam("idDepartement") List<Integer> idDepartements,
                                  @RequestParam("nomDepartement") List<String> nomDepartements) {
        for (int i = 0; i < idDepartements.size(); i++) {
            Departement departement = new Departement();
            departement.setIdDepartement(idDepartements.get(i));
            departement.setNomDepartement(nomDepartements.get(i));
            service.addOrUpdateDepartment(departement);
        }
        return "redirect:/departements/view"; // Redirige vers la gestion des départements
    }
}
