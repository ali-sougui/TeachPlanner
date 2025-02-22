package org.univ.projet_tutore.teachPlanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.univ.projet_tutore.teachPlanner.model.Seance;

import java.util.List;

public interface  SeanceRepository extends JpaRepository< Seance , Integer> {
    // permet de récupérer toutes les séances où l’enseignant est concerné.
    List<Seance> findByNumeroEns(Integer numeroEns);




}
