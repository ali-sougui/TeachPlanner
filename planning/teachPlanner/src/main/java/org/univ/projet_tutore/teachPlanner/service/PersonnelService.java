package org.univ.projet_tutore.teachPlanner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.univ.projet_tutore.teachPlanner.model.Personnel;
import org.univ.projet_tutore.teachPlanner.repository.PersonnelRepository;
import java.util.List;
import java.util.Optional;

@Service
public class PersonnelService {

    private final PersonnelRepository personnelRepository;

    @Autowired
    public PersonnelService(PersonnelRepository personnelRepository) {
        this.personnelRepository = personnelRepository;
    }

    public List<Personnel> getAllPersonnels() {
        return personnelRepository.findAll();
    }

    public Personnel getPersonnelById(Integer id) {
        return personnelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé avec l'ID: " + id));
    }

    @Transactional
    public Personnel addPersonnel(Personnel personnel) {
        // Vérifier si l'email existe déjà
        if (personnelRepository.existsByMail(personnel.getMail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        // S'assurer que l'ID n'est pas défini pour permettre l'auto-génération
        personnel.setIdperso(null);

        return personnelRepository.save(personnel);
    }

    @Transactional
    public Personnel updatePersonnel(Integer id, Personnel personnel) {
        Personnel existingPersonnel = getPersonnelById(id);

        // Vérifier si le nouvel email existe déjà pour un autre utilisateur
        if (!existingPersonnel.getMail().equals(personnel.getMail()) &&
                personnelRepository.existsByMail(personnel.getMail())) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        existingPersonnel.setMail(personnel.getMail());

        // Ne mettre à jour le mot de passe que s'il est fourni
        if (personnel.getMdp() != null && !personnel.getMdp().isEmpty()) {
            existingPersonnel.setMdp(personnel.getMdp());
        }

        existingPersonnel.setRole(personnel.getRole());

        return personnelRepository.save(existingPersonnel);
    }

    @Transactional
    public void deletePersonnel(Integer id) {
        if (!personnelRepository.existsById(id)) {
            throw new RuntimeException("Personnel non trouvé avec l'ID: " + id);
        }
        personnelRepository.deleteById(id);
    }

    public Optional<Personnel> findByEmail(String email) {
        return personnelRepository.findByMail(email);
    }
}
