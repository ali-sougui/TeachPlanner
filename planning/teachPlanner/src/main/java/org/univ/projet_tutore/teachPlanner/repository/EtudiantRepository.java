package org.univ.projet_tutore.teachPlanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.univ.projet_tutore.teachPlanner.model.Etudiant;
import org.univ.projet_tutore.teachPlanner.model.Personnel;

import java.util.List;
import java.util.Optional;

public interface EtudiantRepository extends JpaRepository<Etudiant, Integer> {

    List<Etudiant> findByGroupeId(Integer groupeId);
    Optional<Etudiant> findByPersonnel(Personnel personnel);

}