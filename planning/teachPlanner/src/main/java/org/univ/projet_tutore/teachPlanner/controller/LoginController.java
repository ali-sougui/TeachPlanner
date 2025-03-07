package org.univ.projet_tutore.teachPlanner.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.univ.projet_tutore.teachPlanner.model.Administrateur;
import org.univ.projet_tutore.teachPlanner.model.Enseignant;
import org.univ.projet_tutore.teachPlanner.model.Personnel;
import org.univ.projet_tutore.teachPlanner.service.AdministrateurService;
import org.univ.projet_tutore.teachPlanner.service.EnseignantService;
import org.univ.projet_tutore.teachPlanner.service.PasswordHasher;

@Controller
public class LoginController {
    @Autowired
    private AdministrateurService administrateurService;

    @Autowired
    private EnseignantService enseignantService;

    @GetMapping("/login")
    public String showLoginPage(HttpSession session) {
        if (session.getAttribute("userLoggedIn") != null) {
            return "redirect:/seances";
        }
        return "pages/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        try {
            Personnel personnel = administrateurService.findPersonnelByEmail(email);
            PasswordHasher mdp = new PasswordHasher(password);

            if (mdp.getHashedPassword().equals(personnel.getMdp())) {
                session.setAttribute("userLoggedIn", true);
                session.setAttribute("userEmail", email);
                session.setAttribute("isAdmin", personnel.isAdmin());
                session.setAttribute("userId", personnel.getIdperso());
                session.setAttribute("role", personnel.getRole().toString());

                // Si c'est un enseignant, récupérer ses informations
                if (personnel.getRole() == Personnel.Role.ENSEIGNANT) {
                    Enseignant enseignant = enseignantService.findByPersonnel(personnel);
                    session.setAttribute("nom", enseignant.getNomEns());
                    session.setAttribute("prenom", enseignant.getPrenomEns());
                    session.setAttribute("numeroEns", enseignant.getNumeroEns());
                }
                if (personnel.getRole() == Personnel.Role.ADMIN) {
                    Administrateur admin = administrateurService.findByPersonnel(personnel);
                    session.setAttribute("nom", admin.getNom_admin());
                    session.setAttribute("prenom", admin.getPrenom_admin());
                    session.setAttribute("numeroEns", admin.getId_admin());
                }

                return "redirect:/seances";
            }
        } catch (Exception e) {
            // Log l'erreur si nécessaire
        }

        model.addAttribute("error", "Identifiants invalides");
        return "pages/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}