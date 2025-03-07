package org.univ.projet_tutore.teachPlanner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.univ.projet_tutore.teachPlanner.model.Enseignant;
import org.univ.projet_tutore.teachPlanner.service.EnseignantService;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enseignants")
public class EnseignantController {

    private final EnseignantService enseignantService;

    @Autowired
    public EnseignantController(EnseignantService enseignantService) {
        this.enseignantService = enseignantService;
    }

    @GetMapping
    public ResponseEntity<List<Enseignant>> getAllEnseignants(HttpSession session) {
        // Vérifier si l'utilisateur est connecté et est admin
        if (session.getAttribute("userLoggedIn") == null ||
                !Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(enseignantService.getAllEnseignants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Enseignant> getEnseignantById(@PathVariable Integer id, HttpSession session) {
        // Vérifier si l'utilisateur est connecté et est admin
        if (session.getAttribute("userLoggedIn") == null ||
                !Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Enseignant enseignant = enseignantService.getEnseignantById(id);
            return ResponseEntity.ok(enseignant);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> createEnseignant(@RequestBody Enseignant enseignant, HttpSession session) {
        // Vérifier si l'utilisateur est admin
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Accès non autorisé"));
        }

        try {
            Enseignant savedEnseignant = enseignantService.createEnseignant(enseignant);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEnseignant);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEnseignant(@PathVariable Integer id, HttpSession session) {
        // Vérifier si l'utilisateur est admin
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Accès non autorisé"));
        }

        try {
            enseignantService.deleteEnseignant(id);
            return ResponseEntity.ok(Map.of("message", "Enseignant supprimé avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
