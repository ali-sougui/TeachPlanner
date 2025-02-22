package org.univ.projet_tutore.teachPlanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.univ.projet_tutore.teachPlanner.model.Personnel;



public interface PersonnelRepository extends JpaRepository<Personnel, Integer> {
    Personnel findByMail(String mail); // Trouver un administrateur par son email
}
