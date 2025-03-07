package org.univ.projet_tutore.teachPlanner.controller;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.univ.projet_tutore.teachPlanner.model.Personnel;
import org.univ.projet_tutore.teachPlanner.service.PasswordHasher;
import org.univ.projet_tutore.teachPlanner.service.PersonnelService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/personnels")
public class PersonnelController {

    private final PersonnelService personnelService;

    private static final Logger logger = LoggerFactory.getLogger(PersonnelController.class);
    private PasswordHasher passwordHasher;
    @Autowired
    public PersonnelController(PersonnelService personnelService) {
        this.personnelService = personnelService;
        this.passwordHasher = new PasswordHasher();
    }

    @GetMapping
    public ResponseEntity<List<Personnel>> getAllPersonnels(HttpSession session) {
        // Vérifier si l'utilisateur est connecté et est admin
        if (session.getAttribute("userLoggedIn") == null ||
                !Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(personnelService.getAllPersonnels());
    }


    @GetMapping("/{id}")
    public ResponseEntity<Personnel> getPersonnelById(@PathVariable Integer id, HttpSession session) {
        // Vérifier si l'utilisateur est connecté et est admin
        if (session.getAttribute("userLoggedIn") == null ||
                !Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            Personnel personnel = personnelService.getPersonnelById(id);
            return ResponseEntity.ok(personnel);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/modifyPassword")
    public ResponseEntity<?> modifyMdp(
            @RequestBody Map<String, String> requestBody,
            HttpSession session) {

        logger.info("Requête reçue pour modifier le mot de passe");

        // Récupérer les données du corps de la requête
        String oldPassword = requestBody.get("oldPassword");
        String newPassword = requestBody.get("newPassword");
        String confirmPassword = requestBody.get("confirmPassword");


        System.out.println("Ancien mot de passe : " + oldPassword);
        System.out.println("Nouveau mot de passe : " + newPassword);
        System.out.println("Confirmation du nouveau mot de passe : " + confirmPassword);

        // Vérifier si les données sont présentes
        if (oldPassword == null || newPassword == null || confirmPassword == null) {
            logger.info("Corps de la requête reçu : " + requestBody);
            System.out.println("Données reçues : " + requestBody);
            return ResponseEntity.badRequest().body(Map.of("message", "Données manquantes dans la requête"));
        }

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Utilisateur non connecté"));
        }

        try {
            Personnel personnel = personnelService.getPersonnelById(userId);
            if (personnel == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Personnel non trouvé"));
            }

            if (!passwordHasher.verifyPassword(oldPassword, personnel.getMdp())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Ancien mot de passe incorrect"));
            }

            if (!newPassword.equals(confirmPassword)) {
                return ResponseEntity.badRequest().body(Map.of("message", "Les nouveaux mots de passe ne correspondent pas"));
            }

            if (passwordHasher.verifyPassword(newPassword, personnel.getMdp())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Le nouveau mot de passe doit être différent de l'ancien"));
            }

            String newMdpHashed = passwordHasher.hashPassword(newPassword);
            System.out.println("le mot de passe avant le hash : "+newMdpHashed);
            personnel.setNotMdp(newPassword);



            personnelService.updatePersonnel(userId, personnel);

            return ResponseEntity.ok(Map.of("message", "Mot de passe mis à jour avec succès"));
        } catch (Exception e) {
            logger.error("Erreur lors de la modification du mot de passe : ", e);
            return ResponseEntity.badRequest().body(Map.of("message", "Erreur lors de la modification du mot de passe : " + e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> addPersonnel(@RequestBody Personnel personnel, HttpSession session) {
        // Vérifier si l'utilisateur est admin
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Accès non autorisé"));
        }

        try {
            Personnel savedPersonnel = personnelService.addPersonnel(personnel);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPersonnel);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<?> updatePersonnel(@PathVariable Integer id,
//                                             @RequestBody Personnel personnel,
//                                             HttpSession session) {
//        // Vérifier si l'utilisateur est admin
//        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(Map.of("message", "Accès non autorisé"));
//        }
//
//        try {
//            Personnel updatedPersonnel = personnelService.updatePersonnel(id, personnel);
//            return ResponseEntity.ok(updatedPersonnel);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
//        }
//    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePersonnel(@PathVariable Integer id, @RequestBody Personnel personnel, HttpSession session) {
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Accès non autorisé"));
        }

        try {
            Personnel updatedPersonnel = personnelService.updatePersonnel(id, personnel);
            return ResponseEntity.ok(updatedPersonnel);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePersonnel(@PathVariable Integer id, HttpSession session) {
        // Vérifier si l'utilisateur est admin
        if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Accès non autorisé"));
        }

        try {
            personnelService.deletePersonnel(id);
            return ResponseEntity.ok(Map.of("message", "Personnel supprimé avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model, HttpSession session) {
        if (session.getAttribute("userLoggedIn") == null ||
                !Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return "redirect:/login";
        }

        try {
            Personnel personnel = personnelService.getPersonnelById(id);
            model.addAttribute("personnel", personnel);
            return "pages/editPersonnel"; // Template Thymeleaf pour l'édition
        } catch (RuntimeException e) {
            return "redirect:/personnels/liste";
        }
    }
}
