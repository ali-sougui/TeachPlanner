package org.univ.projet_tutore.teachPlanner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.univ.projet_tutore.teachPlanner.model.Etudiant;
import org.univ.projet_tutore.teachPlanner.service.EtudiantService;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/etudiants")
public class EtudiantController {

    private final EtudiantService etudiantService;

    @Autowired
    public EtudiantController(EtudiantService etudiantService) {
        this.etudiantService = etudiantService;
    }

    @GetMapping
    public ResponseEntity<List<Etudiant>> getAllEtudiants(HttpSession session) {
        // Vérifier si l'utilisateur est connecté et est admin
        if (session.getAttribute("userLoggedIn") == null ||
                !Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<Etudiant> etudiants = etudiantService.getTousLesEtudiants();
        System.out.println("📌 API renvoie " + etudiants.size() + " étudiants.");
        return ResponseEntity.ok(etudiants);
    }

    @PutMapping("/{etudiantId}/assign-groupe/{groupeId}")
    public ResponseEntity<?> assignEtudiantToGroupe(@PathVariable Integer etudiantId, @PathVariable Integer groupeId) {
        try {
            etudiantService.assignEtudiantToGroupe(etudiantId, groupeId);
            return ResponseEntity.ok(Map.of("message", "Étudiant ajouté au groupe avec succès"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ Récupérer tous les étudiants d’un groupe en JSON
    @GetMapping("/groupe/{id}")
    public ResponseEntity<List<Etudiant>> getEtudiantsByGroupe(@PathVariable Integer id) {
        List<Etudiant> etudiants = etudiantService.getEtudiantsByGroupe(id);
        return ResponseEntity.ok(etudiants);
    }

    @PostMapping
    public ResponseEntity<?> createEtudiant(@RequestBody Etudiant etudiant, HttpSession session) {
        System.out.println("Données reçues  coté backend : " + etudiant.getNom()+etudiant.getNumeroEtudiant()+etudiant.getPrenom()+etudiant.getPersonnel().getIdperso());
        if (!checkAdminAccess(session)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Action non autorisée"));
        }

        try {
            Etudiant savedEtudiant = etudiantService.createEtudiant(etudiant);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedEtudiant);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }



    @GetMapping("/{id}")
    public ResponseEntity<Etudiant> getEtudiantById(@PathVariable Integer id, HttpSession session) {
        // Vérifier si l'utilisateur est connecté et est admin ou l'étudiant lui-même
        if (session.getAttribute("userLoggedIn") == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Si c'est un étudiant, vérifier qu'il accède à ses propres données
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin")) &&
                !id.equals(session.getAttribute("userId"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Etudiant etudiant = etudiantService.getEtudiantById(id);
            return ResponseEntity.ok(etudiant);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/groupe/{groupeId}")
    public ResponseEntity<List<Etudiant>> getEtudiantsByGroupe(@PathVariable Integer groupeId, HttpSession session) {
        // Vérifier si l'utilisateur est connecté et est admin
        if (session.getAttribute("userLoggedIn") == null ||
                !Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(etudiantService.getEtudiantsByGroupe(groupeId));
    }


    private boolean checkAdminAccess(HttpSession session) {
        return session.getAttribute("userLoggedIn") != null &&
                Boolean.TRUE.equals(session.getAttribute("isAdmin"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEtudiant(@PathVariable Integer id, HttpSession session) {
        // Vérifier si l'utilisateur est admin
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Accès non autorisé"));
        }

        try {
            etudiantService.deleteEtudiant(id);
            return ResponseEntity.ok(Map.of("message", "Étudiant supprimé avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
