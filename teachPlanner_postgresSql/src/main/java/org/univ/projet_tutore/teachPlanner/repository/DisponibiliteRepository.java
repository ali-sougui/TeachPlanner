package org.univ.projet_tutore.teachPlanner.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.univ.projet_tutore.teachPlanner.model.Disponibilite;

import java.util.List;

public interface DisponibiliteRepository extends JpaRepository<Disponibilite, Long> {
    List<Disponibilite> findByNumeroEns(Integer numeroEns);
    void deleteByNumeroEnsAndJour(Integer numeroEns, Disponibilite.JourSemaine jour);
}