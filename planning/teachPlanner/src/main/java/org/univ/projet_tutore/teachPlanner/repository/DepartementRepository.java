package org.univ.projet_tutore.teachPlanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.univ.projet_tutore.teachPlanner.model.Departement;

/*
 * Dans le Spring Data JPA , un repository est une interface qui hérite
 * d'une interface de base fournie par Spring(comme JpaRepository).
 * Cela évite d'écrire manuellement les requêtes pour les opérations courantes.
 * 
 * 
 * L'interface JpaRepository permet d'accéder aux fonctionnalités CRUD
 * ainsi qu'à des méthodes avancées pour les requêtes personnalisées.
 * Elle prend deux paramètres génériques:
 * La classe d'entité.
 * Le type de la clé primaire.
*/


@Repository
public interface DepartementRepository extends JpaRepository<Departement, Integer> {

}
