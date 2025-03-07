package org.univ.projet_tutore.teachPlanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.univ.projet_tutore.teachPlanner.model.Matiere;

@Repository
public interface MatiereRepository extends JpaRepository<Matiere,Integer> {
	
}
