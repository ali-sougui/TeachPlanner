package org.univ.projet_tutore.teachPlanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.univ.projet_tutore.teachPlanner.model.Groupe;

public interface GroupeRepository extends JpaRepository<Groupe, Integer> {
}
