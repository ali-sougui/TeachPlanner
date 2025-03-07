package org.univ.projet_tutore.teachPlanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.univ.projet_tutore.teachPlanner.model.Salle;

public interface SalleRepository extends JpaRepository<Salle, Integer> {
    // Vous pouvez ajouter des méthodes de recherche personnalisées si nécessaire.
}
