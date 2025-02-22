package org.univ.projet_tutore.teachPlanner.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.univ.projet_tutore.teachPlanner.model.Disponibilite;
import org.univ.projet_tutore.teachPlanner.repository.DisponibiliteRepository;

import java.util.List;

@Service
public class DisponibiliteService {

    private final DisponibiliteRepository disponibiliteRepository;

    @Autowired
    public DisponibiliteService(DisponibiliteRepository disponibiliteRepository) {
        this.disponibiliteRepository = disponibiliteRepository;
    }

    public List<Disponibilite> getDisponibilitesByEnseignant(Integer numeroEns) {
        return disponibiliteRepository.findByNumeroEns(numeroEns);
    }

    public Disponibilite ajouterDisponibilite(Disponibilite disponibilite) {
        return disponibiliteRepository.save(disponibilite);
    }

    @Transactional
    public void supprimerDisponibilite(Integer numeroEns, Disponibilite.JourSemaine jour) {
        disponibiliteRepository.deleteByNumeroEnsAndJour(numeroEns, jour);
    }

    public List<Disponibilite> getAllDisponibilites() {
        return disponibiliteRepository.findAll();
    }
}