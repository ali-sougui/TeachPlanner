package org.univ.projet_tutore.teachPlanner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.univ.projet_tutore.teachPlanner.model.Personnel;
import org.univ.projet_tutore.teachPlanner.repository.PersonnelRepository;

import java.util.List;

@Service
public class PersonnelService {

    @Autowired
    private PersonnelRepository personnelRepository;

    public List<Personnel> getAllPersonnels() {
        return personnelRepository.findAll();
    }

    public Personnel getPersonnelById(Integer id) {
        return personnelRepository.findById(id).orElse(null);
    }

    public Personnel addPersonnel(Personnel personnel) {
        return personnelRepository.save(personnel);
    }

    public Personnel updatePersonnel(Integer id, Personnel personnelDetails) {
        Personnel personnel = personnelRepository.findById(id).orElse(null);
        if (personnel != null) {
            // Mise à jour du mail
            personnel.setMail(personnelDetails.getMail());

            // Si un mot de passe a été fourni, on le hache
            if (personnelDetails.getMdp() != null && !personnelDetails.getMdp().isEmpty()) {

                System.out.println("Mot de passe avant hachage : " + personnelDetails.getMdp());
                PasswordHasher mdp = new PasswordHasher(personnelDetails.getMdp());
                System.out.println("Mot de passe hashé : " + mdp.getHashedPassword());

            }

            // Sauvegarder les changements dans la base de données
            return personnelRepository.save(personnel);
        }
        return null;
    }


    public void deletePersonnel(Integer id) {
        personnelRepository.deleteById(id);
    }
}
