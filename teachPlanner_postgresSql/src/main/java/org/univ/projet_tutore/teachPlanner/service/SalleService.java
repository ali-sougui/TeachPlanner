package org.univ.projet_tutore.teachPlanner.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.univ.projet_tutore.teachPlanner.model.Salle;
import org.univ.projet_tutore.teachPlanner.repository.SalleRepository;


import java.util.List;


@Service
public class SalleService {

    @Autowired
    private SalleRepository salleRepository;

    @Autowired
    private EntityManager entityManager;

    // Obtenir toutes les salles
    public List<Salle> getAllSalles() {
        return salleRepository.findAll();
    }

    // Ajouter une nouvelle salle
    @Transactional
    public Salle addSalle(Salle salle) {
        try {
            Query query = entityManager.createNativeQuery("INSERT INTO salles (numero_salle,nom_salle, type_salle) VALUES (:numSalle,:nomSalle, CAST(:typeSalle AS type_salle_enum))");
            query.setParameter("numSalle", salle.getNumSalle());
            query.setParameter("nomSalle", salle.getNomSalle());
            query.setParameter("typeSalle", salle.getTypeSalle().name());
            int result = query.executeUpdate();
            System.out.println("Insertion effectuée, lignes affectées : " + result);
            return salle;
        } catch (Exception e) {
            System.out.println("Erreur lors de l'insertion : " + e.getMessage());
            throw e;
        }
    }

    // Obtenir une salle par son ID
    public Salle getSalleById(Integer id) {
        return salleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Salle introuvable avec l'ID : " + id));
    }

    // Supprimer une salle par son ID
    @Transactional
    public void supprimerSalle(Integer id) {
        System.out.println("Tentative de suppression de la salle avec l'ID : " + id);
        if (salleRepository.existsById(id)) {
            System.out.println("Salle trouvée, suppression en cours...");
            salleRepository.deleteById(id);
            System.out.println("Salle supprimée avec succès, ID : " + id);
        } else {
            System.out.println("Salle introuvable avec l'ID : " + id);
            throw new RuntimeException("Salle introuvable avec l'ID : " + id);
        }
    }


    // Dans votre SalleService, si vous avez accès à la requête SQL
    // Mettre à jour une salle
    @Transactional
    public void updateSalle(Salle salle) {
        System.out.println("Type de la variable type : " + salle.getTypeSalle().getClass().getName());
        try {
            Query query = entityManager.createNativeQuery(
                    "UPDATE salles SET nom_salle = :nomSalle, type_salle = CAST(:typeSalle AS type_salle_enum) WHERE numero_salle = :numSalle"
            );
            query.setParameter("numSalle", salle.getNumSalle());
            query.setParameter("nomSalle", salle.getNomSalle());
            query.setParameter("typeSalle", salle.getTypeSalle().name()); // Utilisation de name() pour obtenir une chaîne

            int result = query.executeUpdate();
            System.out.println("Mise à jour effectuée, lignes affectées : " + result);
        } catch (Exception e) {
            System.out.println("Erreur lors de la mise à jour : " + e.getMessage());
            throw e;
        }
    }

//        Salle existingSalle = salleRepository.findById(updatedSalle.getNumSalle())
//                .orElseThrow(() -> new RuntimeException("Salle introuvable avec l'ID : " + updatedSalle.getNumSalle()));
//
//        // Mettre à jour les champs nécessaires
//        existingSalle.setNomSalle(updatedSalle.getNomSalle());
//        existingSalle.setTypeSalle(updatedSalle.getTypeSalle());
//
//        salleRepository.save(existingSalle);
//     System.out.println("Salle mise à jour avec succès, ID : " + existingSalle.getNumSalle());


}
