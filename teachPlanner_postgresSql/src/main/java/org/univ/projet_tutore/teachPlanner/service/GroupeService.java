package org.univ.projet_tutore.teachPlanner.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.univ.projet_tutore.teachPlanner.model.Groupe;
import org.univ.projet_tutore.teachPlanner.repository.GroupeRepository;

import java.util.List;

@Service
public class GroupeService {
    @Autowired
    private GroupeRepository groupeRepository;

    @Autowired
    private EntityManager entityManager;

    // Obtenir tous les groupes
    public List<Groupe> getAllGroupes() {
        return groupeRepository.findAll();
    }

    // Ajouter un nouveau groupe
    @Transactional
    public Groupe addGroupe(Groupe groupe) {
        try {
            // Modifier la requête pour que le CAST soit effectué directement dans la requête SQL
            Query query = entityManager.createNativeQuery(
                    "INSERT INTO groupes (num_groupe, type_cours, code_matiere, nom_groupe) " +
                            "VALUES (:numGroupe, CAST(:typeCours AS type_cours_enum), :codeMatiere, :nomGroupe)");

            // Passer les paramètres comme d'habitude
            query.setParameter("numGroupe", groupe.getNum_groupe());
            query.setParameter("typeCours", groupe.getType_cours().name());  // On passe le nom de l'énumération
            query.setParameter("codeMatiere", groupe.getCodeMatiere());
            query.setParameter("nomGroupe", groupe.getNom_groupe());

            int result = query.executeUpdate();
            System.out.println("Insertion effectuée, lignes affectées : " + result);
            return groupe;
        } catch (Exception e) {
            System.out.println("Erreur lors de l'insertion : " + e.getMessage());
            throw e;
        }
    }


    // Obtenir un groupe par son ID
    public Groupe getGroupeById(Integer id) {
        return groupeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Groupe introuvable avec l'ID : " + id));
    }

    // Supprimer un groupe par son ID
    @Transactional
    public void supprimerGroupe(Integer id) {
        if (groupeRepository.existsById(id)) {
            groupeRepository.deleteById(id);
            System.out.println("Groupe supprimé avec succès, ID : " + id);
        } else {
            throw new RuntimeException("Groupe introuvable avec l'ID : " + id);
        }
    }
}
