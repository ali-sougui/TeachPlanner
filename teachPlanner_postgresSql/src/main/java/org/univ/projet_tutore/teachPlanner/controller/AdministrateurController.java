package org.univ.projet_tutore.teachPlanner.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.univ.projet_tutore.teachPlanner.model.Administrateur;
import org.univ.projet_tutore.teachPlanner.model.Personnel;
import org.univ.projet_tutore.teachPlanner.service.AdministrateurService;
import org.univ.projet_tutore.teachPlanner.service.PasswordHasher;

import java.util.List;

@Controller
@RequestMapping("/administrateurs")
public class AdministrateurController {

    @Autowired
    private AdministrateurService administrateurService;

    // Afficher la page de connexion ou la liste des administrateurs
    @GetMapping
    public String showLoginPage(HttpSession session, Model model) {
        if (session.getAttribute("adminLoggedIn") != null) {
            // Si l'administrateur est déjà connecté, on le redirige vers la page de gestion des personnels
            return "redirect:/personnels/liste";
        }
        // Si l'administrateur n'est pas connecté, on montre la page de connexion
        return "index"; // Cette page doit afficher le formulaire de connexion
    }

    // Page de gestion des personnels, protégée par authentification
    @GetMapping("/personnels/liste")
    public String gestionPersonnels(Model model, HttpSession session) {
        // Vérifier si l'administrateur est authentifié
        if (session.getAttribute("adminLoggedIn") == null) {
            return "redirect:/"; // Si non, redirection vers la page de connexion
        }


        // Si l'administrateur est connecté, on affiche la page des gestionnaires
        model.addAttribute("administrateurs", administrateurService.getAllAdministrateurs());
        return "pages/gestion_personnels";
    }

    // Traiter la soumission du formulaire de connexion
    @PostMapping("/login")
    public String loginAdmin(@RequestParam String email, @RequestParam String password, HttpSession session,Model model) {
        Personnel personnel = administrateurService.findAdministrateurByEmail(email);

        if (personnel != null) {
            PasswordHasher mdp = new PasswordHasher(password);
            if (mdp.getHashedPassword().equals(personnel.getMdp())) {
                session.setAttribute("userLoggedIn", true);
                session.setAttribute("userEmail", email);
                session.setAttribute("isAdmin", email.equals("admin@gmail.com"));
                session.setAttribute("userId", personnel.getIdperso());
                return "redirect:/seances";
            }
        }
        model.addAttribute("error", "Identifiants invalides");
        return "redirect:/";
    }
    @GetMapping("/seances")
    public String afficherSeances(Model model, HttpSession session) {
        if (session.getAttribute("userLoggedIn") == null) {
            return "redirect:/"; // Redirection si non connecté
        }

        boolean isAdmin = Boolean.TRUE.equals(session.getAttribute("isAdmin"));
        Integer userId = (Integer) session.getAttribute("userId");

        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("userId", userId);
        System.out.println("isAdmin: " + session.getAttribute("isAdmin"));
        System.out.println("userId: " + session.getAttribute("userId"));
        System.out.println("userEmail: " + session.getAttribute("userEmail"));

        return "pages/seance";
    }



    // Méthode de déconnexion
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Invalider la session, déconnecter l'utilisateur
        return "redirect:/administrateurs"; // Rediriger vers la page de connexion
    }

    @GetMapping("/{id}")
    @ResponseBody // Permet de renvoyer des données JSON si nécessaire
    public Administrateur getAdministrateurById(@PathVariable Integer id) {
        return administrateurService.getAdministrateurById(id);
    }

    @PostMapping
    @ResponseBody
    public Administrateur addAdministrateur(@RequestBody Administrateur administrateur) {
        return administrateurService.addAdministrateurs(administrateur);
    }

    @PutMapping("/{id}")
    @ResponseBody
    public Administrateur updateAdministrateur(@PathVariable Integer id, @RequestBody Administrateur administrateurDetails) {
        return administrateurService.updateAdministrateur(id, administrateurDetails);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public void deleteAdministrateur(@PathVariable Integer id) {
        administrateurService.deleteAdministrateur(id);
    }
}
