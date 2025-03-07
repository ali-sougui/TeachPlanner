package org.univ.projet_tutore.teachPlanner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.univ.projet_tutore.teachPlanner.model.Administrateur;
import org.univ.projet_tutore.teachPlanner.model.Enseignant;
import org.univ.projet_tutore.teachPlanner.model.Personnel;
import org.univ.projet_tutore.teachPlanner.repository.AdministrateurRepository;
import org.univ.projet_tutore.teachPlanner.repository.EnseignantRepository;
import org.univ.projet_tutore.teachPlanner.repository.PersonnelRepository;

@Service
public class AdministrateurService {
    @Autowired
    private PersonnelRepository personnelRepository;
    @Autowired
    private AdministrateurRepository administrateurRepository;

    @Autowired
    private EnseignantRepository enseignantRepository;

    public Personnel findPersonnelByEmail(String email) {
        return personnelRepository.findByMail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
    public Administrateur createAdmin(Administrateur admin) {
        if (admin == null || admin.getPersonnel() == null || admin.getPersonnel().getIdperso() == null) {
            throw new IllegalArgumentException("Les informations de l'administrateur sont incomplètes");
        }
        return administrateurRepository.save(admin);
    }


    @Transactional
    public Enseignant createEnseignant(String email, String password, String nom, String prenom) {
        // Créer le compte personnel
        Personnel personnel = new Personnel();
        personnel.setMail(email);
        personnel.setMdp(password);
        personnel.setRole(Personnel.Role.ENSEIGNANT);
        personnel = personnelRepository.save(personnel);

        // Créer l'enseignant
        Enseignant enseignant = new Enseignant();
        enseignant.setNomEns(nom);
        enseignant.setPrenomEns(prenom);
        enseignant.setPersonnel(personnel);
        return enseignantRepository.save(enseignant);
    }

    @Transactional
    public void deleteEnseignant(Integer numeroEns) {
        Enseignant enseignant = enseignantRepository.findById(Long.valueOf(numeroEns))
                .orElseThrow(() -> new RuntimeException("Enseignant non trouvé"));

        // Supprimer d'abord l'enseignant
        enseignantRepository.delete(enseignant);

        // Puis supprimer le compte personnel associé
        if (enseignant.getPersonnel() != null) {
            personnelRepository.delete(enseignant.getPersonnel());
        }
    }

    public boolean isAdmin(Personnel personnel) {
        return personnel != null && personnel.getRole() == Personnel.Role.ADMIN;
    }

    public Administrateur findByPersonnel(Personnel personnel) {
        return administrateurRepository.findByPersonnel(personnel)
                .orElseThrow(() -> new RuntimeException("Administrateur non trouvé pour cet utilisateur."));
    }
}