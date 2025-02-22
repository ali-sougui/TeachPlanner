package org.univ.projet_tutore.teachPlanner.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.univ.projet_tutore.teachPlanner.model.Disponibilite;
import org.univ.projet_tutore.teachPlanner.service.DisponibiliteService;

import java.util.List;

@RestController
@RequestMapping("/api/disponibilites")
public class DisponibiliteController {

    private final DisponibiliteService disponibiliteService;

    @Autowired
    public DisponibiliteController(DisponibiliteService disponibiliteService) {
        this.disponibiliteService = disponibiliteService;
    }

    @GetMapping("/enseignant/{numeroEns}")
    public ResponseEntity<List<Disponibilite>> getDisponibilitesByEnseignant(@PathVariable Integer numeroEns) {
        List<Disponibilite> disponibilites = disponibiliteService.getDisponibilitesByEnseignant(numeroEns);
        return ResponseEntity.ok(disponibilites);
    }

    @PostMapping
    public ResponseEntity<Disponibilite> ajouterDisponibilite(@RequestBody Disponibilite disponibilite) {
        Disponibilite nouvelleDisponibilite = disponibiliteService.ajouterDisponibilite(disponibilite);
        return ResponseEntity.ok(nouvelleDisponibilite);
    }

    @DeleteMapping("/enseignant/{numeroEns}/jour/{jour}")
    public ResponseEntity<Void> supprimerDisponibilite(
            @PathVariable Integer numeroEns,
            @PathVariable Disponibilite.JourSemaine jour) {
        disponibiliteService.supprimerDisponibilite(numeroEns, jour);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Disponibilite>> getAllDisponibilites() {
        List<Disponibilite> disponibilites = disponibiliteService.getAllDisponibilites();
        return ResponseEntity.ok(disponibilites);
    }
}