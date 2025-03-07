package org.univ.projet_tutore.teachPlanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.univ.projet_tutore.teachPlanner.model.Administrateur;
import org.univ.projet_tutore.teachPlanner.model.Personnel;

import java.util.Optional;

public interface AdministrateurRepository extends JpaRepository<Administrateur, Integer> {
    Optional<Administrateur> findByPersonnel(Personnel personnel);
}
