package org.univ.projet_tutore.teachPlanner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.univ.projet_tutore.teachPlanner.model.Administrateur;
import org.univ.projet_tutore.teachPlanner.model.Personnel;
import org.univ.projet_tutore.teachPlanner.repository.AdministrateurRepository;
import org.univ.projet_tutore.teachPlanner.repository.PersonnelRepository;

import java.util.List;

@Service
public class AdministrateurService {
    @Autowired
    private AdministrateurRepository administrateurRepository;

    @Autowired
    private PersonnelRepository personnelRepository;

    public List<Administrateur> getAllAdministrateurs() {
        return administrateurRepository.findAll();
    }

    public Administrateur getAdministrateurById(Integer id) {
        return administrateurRepository.findById(id).orElse(null);
    }

    public Personnel findAdministrateurByEmail(String email) {
        Personnel personnel = personnelRepository.findByMail(email);
        if (personnel == null) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        return personnel;
    }

    public boolean isAdmin(String email) {
        return "admin@gmail.com".equals(email);
    }

    public Administrateur addAdministrateurs(Administrateur administrateur) {
        if (administrateur == null) {
            throw new RuntimeException("L'administrateur ne peut pas être null");
        }
        return administrateurRepository.save(administrateur);
    }

    public Administrateur updateAdministrateur(Integer id, Administrateur administrateurDetails) {
        Administrateur administrateur = administrateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrateur non trouvé"));

        administrateur.setNom_admin(administrateurDetails.getNom_admin());
        administrateur.setPrenom_admin(administrateurDetails.getPrenom_admin());
        administrateur.setPersonnel(administrateurDetails.getPersonnel());

        return administrateurRepository.save(administrateur);
    }

    public void deleteAdministrateur(Integer id) {
        if (!administrateurRepository.existsById(id)) {
            throw new RuntimeException("Administrateur non trouvé");
        }
        administrateurRepository.deleteById(id);
    }
}
