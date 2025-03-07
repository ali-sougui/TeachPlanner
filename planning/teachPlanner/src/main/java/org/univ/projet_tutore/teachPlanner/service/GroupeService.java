package org.univ.projet_tutore.teachPlanner.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.univ.projet_tutore.teachPlanner.model.Groupe;
import org.univ.projet_tutore.teachPlanner.repository.GroupeRepository;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupeService {

    private final GroupeRepository groupeRepository;

    @Autowired
    public GroupeService(GroupeRepository groupeRepository) {
        this.groupeRepository = groupeRepository;
    }

    public List<Groupe> getAllGroupes() {
        return groupeRepository.findAll();
    }

    public List<Groupe> getGroupesByAnneeScolaire(String anneeScolaire) {
        return groupeRepository.findByAnneeScolaire(anneeScolaire);
    }

    @Transactional
    public Groupe createGroupe(Groupe groupe) {
        if (groupeRepository.existsByNomAndAnneeScolaire(groupe.getNom(), groupe.getAnneeScolaire())) {
            throw new RuntimeException("Un groupe avec ce nom existe déjà pour cette année scolaire");
        }
        return groupeRepository.save(groupe);
    }

    @Transactional
    public void deleteGroupe(Integer id) {
        groupeRepository.deleteById(id);
    }

    public Groupe getGroupeById(Integer id) {
        return groupeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Groupe non trouvé"));
    }
}