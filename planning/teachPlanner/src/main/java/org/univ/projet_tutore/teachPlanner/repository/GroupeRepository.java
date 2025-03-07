package org.univ.projet_tutore.teachPlanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.univ.projet_tutore.teachPlanner.model.Groupe;


import java.util.List;

public interface GroupeRepository extends JpaRepository<Groupe, Integer> {
    List<Groupe> findByAnneeScolaire(String anneeScolaire);
    boolean existsByNomAndAnneeScolaire(String nom, String anneeScolaire);
}