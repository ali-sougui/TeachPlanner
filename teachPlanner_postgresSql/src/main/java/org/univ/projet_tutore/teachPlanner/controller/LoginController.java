package org.univ.projet_tutore.teachPlanner.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.univ.projet_tutore.teachPlanner.model.Personnel;
import org.univ.projet_tutore.teachPlanner.service.AdministrateurService;
import org.univ.projet_tutore.teachPlanner.service.PasswordHasher;

@Controller
public class LoginController {
    @Autowired
    private AdministrateurService administrateurService;

    @GetMapping("/login")
    public String showLoginPage(HttpSession session) {
        // Si déjà connecté, rediriger vers la page d'accueil
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
        // Vérifier les identifiants (à adapter selon votre logique d'authentification)
        if (isValidCredentials(email, password,model,session)) {
            // Stocker les informations de l'utilisateur en session

            // Ajouter d'autres attributs selon vos besoins (isAdmin, userId, etc.)

            return "redirect:/seances";
        } else {
            model.addAttribute("error", "Identifiants invalides");
            return "pages/login";
        }
    }

    private boolean isValidCredentials(String email, String password,Model model, HttpSession session) {
        // Implémenter votre logique de vérification des identifiants
        // Par exemple, vérifier dans la base de données
        Personnel personnel = administrateurService.findAdministrateurByEmail(email);

        if (personnel != null) {
            PasswordHasher mdp = new PasswordHasher(password);
            if (mdp.getHashedPassword().equals(personnel.getMdp())) {
                session.setAttribute("userLoggedIn", true);
                session.setAttribute("userEmail", email);
                session.setAttribute("isAdmin", email.equals("admin@gmail.com"));
                session.setAttribute("userId", personnel.getIdperso());
                return true;
            }
        }
        model.addAttribute("error", "Identifiants invalides");
        return false;
    }
}
