package org.univ.projet_tutore.teachPlanner.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.univ.projet_tutore.teachPlanner.model.Administrateur;
import org.univ.projet_tutore.teachPlanner.service.AdministrateurService;

import java.util.Map;

@RestController
@RequestMapping("")
public class AdminController {

    @Autowired
    private AdministrateurService administrateurService;

    @PostMapping("/api/administrateurs")
    public ResponseEntity<?> createAdmin(@RequestBody Administrateur admin) {
        System.out.println("Données reçues côté backend : " +
                "Nom: " + admin.getNom_admin() +
                ", Prénom: " + admin.getPrenom_admin() +
                ", Personnel: " + (admin.getPersonnel() != null ? admin.getPersonnel().getIdperso() : "null"));

        if (admin.getNom_admin() == null || admin.getPrenom_admin() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Le nom et le prénom sont obligatoires"));
        }

        try {
            Administrateur savedAdmin = administrateurService.createAdmin(admin);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAdmin);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

}
