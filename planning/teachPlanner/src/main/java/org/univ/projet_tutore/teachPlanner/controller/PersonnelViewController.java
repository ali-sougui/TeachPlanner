package org.univ.projet_tutore.teachPlanner.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.univ.projet_tutore.teachPlanner.model.Personnel;
import org.univ.projet_tutore.teachPlanner.service.PersonnelService;

@Controller
@RequestMapping("/personnels")
public class PersonnelViewController {
    private final PersonnelService personnelService;
    @Autowired
    public PersonnelViewController(PersonnelService personnelService) {
        this.personnelService = personnelService;
    }

    @GetMapping("/liste")
    public String listPersonnels(HttpSession session) {
        // Vérifier si l'utilisateur est connecté et est admin
        if (session.getAttribute("userLoggedIn") == null ||
                !Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return "redirect:/login";
        }
        return "pages/listePersonnels"; // Nom du template Thymeleaf
    }

    @GetMapping("/nouveau")
    public String showFormulaire(HttpSession session) {
        // Vérifier si l'utilisateur est connecté et est admin
        if (session.getAttribute("userLoggedIn") == null ||
                !Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
            return "redirect:/login";
        }
        return "pages/personnels"; // Nom du template Thymeleaf
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
            return "pages/editPersonnels"; // Template Thymeleaf pour l'édition
        } catch (RuntimeException e) {
            return "redirect:/personnels/liste";
        }
    }
}