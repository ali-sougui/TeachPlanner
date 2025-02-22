package org.univ.projet_tutore.teachPlanner.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.univ.projet_tutore.teachPlanner.model.Enseignant;

public interface EnseignantRepository extends JpaRepository<Enseignant,Long>{
	
	// Recherche par nom ou prénom contenant le terme donné
	List<Enseignant> findByNomContainingOrPrenomContaining(String nom, String prenom);

	Optional<Enseignant> findByIdperso(Integer idperso);

	@Query("SELECT e FROM Enseignant e WHERE e.id = :id")
	Enseignant findEnseignantWithDetails(@Param("id") Long id);
}
