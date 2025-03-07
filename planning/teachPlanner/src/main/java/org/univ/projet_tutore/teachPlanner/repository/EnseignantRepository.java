package org.univ.projet_tutore.teachPlanner.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.univ.projet_tutore.teachPlanner.model.Enseignant;
import org.univ.projet_tutore.teachPlanner.model.Personnel;

public interface EnseignantRepository extends JpaRepository<Enseignant,Long>{

	// Recherche par nom ou prénom contenant le terme donné
	List<Enseignant> findByNomEnsContainingOrPrenomEnsContaining(String nom, String prenom);

	//	Optional<Enseignant> findByIdperso(Integer idperso);
	Optional<Enseignant> findByPersonnel(Optional<Personnel> perso);

	Enseignant findByPersonnel(Personnel personnel);

	@Query("SELECT e FROM Enseignant e WHERE e.id = :id")
	Enseignant findEnseignantWithDetails(@Param("id") Long id);




}