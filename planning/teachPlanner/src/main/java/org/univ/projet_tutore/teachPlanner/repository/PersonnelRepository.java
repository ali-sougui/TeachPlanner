package org.univ.projet_tutore.teachPlanner.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.univ.projet_tutore.teachPlanner.model.Personnel;
import java.util.Optional;

public interface PersonnelRepository extends JpaRepository<Personnel, Integer> {
    Optional<Personnel> findByMail(String mail);
    boolean existsByMail(String mail);
}
