package org.univ.projet_tutore.teachPlanner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.univ.projet_tutore.teachPlanner.model.Etudiant;
import org.univ.projet_tutore.teachPlanner.model.Groupe;
import org.univ.projet_tutore.teachPlanner.service.EtudiantService;
import org.univ.projet_tutore.teachPlanner.service.GroupeService;

import java.util.List;

@Controller
@RequestMapping("/etudiants")
public class EtudiantViewController {

    private final EtudiantService etudiantService;
    private final GroupeService groupeService;

    @Autowired
    public EtudiantViewController(EtudiantService etudiantService, GroupeService groupeService) {
        this.etudiantService = etudiantService;
        this.groupeService = groupeService;
    }

    @GetMapping("/groupe/{id}")
    public String afficherEtudiantsParGroupe(@PathVariable Integer id, Model model) {
        List<Etudiant> etudiants = etudiantService.getEtudiantsByGroupe(id);
        Groupe groupe = groupeService.getGroupeById(id); // 🔥 Récupération du groupe

        model.addAttribute("etudiants", etudiants);
        model.addAttribute("groupe", groupe); // 🔥 Ajout du groupe au modèle

        return "pages/etudiants-groupes";  // Assurez-vous que pages/etudiants-groupes.html existe
    }
    @GetMapping("/groupe/{id}/ajouter")
    public String afficherFormulaireAjout(@PathVariable Integer id, Model model) {
        List<Etudiant> tousLesEtudiants = etudiantService.getTousLesEtudiants();
        Groupe groupe = groupeService.getGroupeById(id);

        model.addAttribute("groupe", groupe);
        model.addAttribute("tousLesEtudiants", tousLesEtudiants);

        return "pages/etudiants-groupes";
    }

}

