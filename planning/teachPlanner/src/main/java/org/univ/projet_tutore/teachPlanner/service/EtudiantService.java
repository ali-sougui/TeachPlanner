package org.univ.projet_tutore.teachPlanner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.univ.projet_tutore.teachPlanner.model.Etudiant;
import org.univ.projet_tutore.teachPlanner.model.Groupe;
import org.univ.projet_tutore.teachPlanner.model.Personnel;
import org.univ.projet_tutore.teachPlanner.repository.EtudiantRepository;
import org.univ.projet_tutore.teachPlanner.repository.GroupeRepository;
import org.univ.projet_tutore.teachPlanner.repository.PersonnelRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;
    private final PersonnelRepository personnelRepository;
    private final GroupeRepository groupeRepository;

    @Autowired
    public EtudiantService(EtudiantRepository etudiantRepository, PersonnelRepository personnelRepository, GroupeRepository groupeRepository) {
        this.etudiantRepository = etudiantRepository;
        this.personnelRepository = personnelRepository;
        this.groupeRepository = groupeRepository;
    }


    public List<Etudiant> getAllEtudiants() {
        return etudiantRepository.findAll();
    }

    public void assignEtudiantToGroupe(Integer etudiantId, Integer groupeId) {
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        Groupe groupe = groupeRepository.findById(groupeId)
                .orElseThrow(() -> new RuntimeException("Groupe non trouvé"));

        etudiant.setGroupe(groupe);
        etudiantRepository.save(etudiant);
    }

    public List<Etudiant> getEtudiantsByGroupe(Integer groupeId) {
        return etudiantRepository.findByGroupeId(groupeId);
    }


    @Transactional
    public Etudiant createEtudiant(String nom, String prenom, Personnel personnel, Groupe groupe) {
        // Vérifier que le personnel a le rôle ETUDIANT
        if (personnel.getRole() != Personnel.Role.ETUDIANT) {
            throw new RuntimeException("Le personnel doit avoir le rôle ETUDIANT");
        }

        // Vérifier qu'il n'existe pas déjà un étudiant pour ce personnel
        if (etudiantRepository.findByPersonnel(personnel).isPresent()) {
            throw new RuntimeException("Un étudiant existe déjà pour ce personnel");
        }

        Etudiant etudiant = new Etudiant();
        etudiant.setNom(nom);
        etudiant.setPrenom(prenom);
        etudiant.setPersonnel(personnel);
        etudiant.setGroupe(groupe);
        etudiant.setCreatedAt(LocalDateTime.now());

        // Assurez-vous que l'ID est null pour permettre l'auto-génération
        etudiant.setNumeroEtudiant(null);

        return etudiantRepository.save(etudiant);
    }


    @Transactional
    public void deleteEtudiant(Integer numeroEtudiant) {
        Etudiant etudiant = etudiantRepository.findById(numeroEtudiant)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        // Supprimer d'abord l'étudiant
        etudiantRepository.delete(etudiant);

        // Puis supprimer le compte personnel associé
        if (etudiant.getPersonnel() != null) {
            personnelRepository.delete(etudiant.getPersonnel());
        }
    }

    public Etudiant getEtudiantById(Integer numeroEtudiant) {
        return etudiantRepository.findById(numeroEtudiant)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));
    }

    public Etudiant findByPersonnel(Integer idPersonnel) {
        Personnel personnel = personnelRepository.findById(idPersonnel)
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé"));
        return etudiantRepository.findByPersonnel(personnel)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));
    }
    @Transactional
    public Etudiant createEtudiant(String email, String password, String nom, String prenom) {
        // Vérifier si l'email existe déjà
        if (personnelRepository.existsByMail(email)) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        // Créer le compte personnel
        Personnel personnel = new Personnel();
        personnel.setMail(email);
        personnel.setMdp(password);
        personnel.setRole(Personnel.Role.ETUDIANT);
        personnel = personnelRepository.save(personnel);

        // Créer l'étudiant
        Etudiant etudiant = new Etudiant(nom, prenom);
        etudiant.setPersonnel(personnel);
        return etudiantRepository.save(etudiant);
    }

    @Transactional
    public Etudiant createEtudiant(Etudiant etudiant) {
        // Vérifier si le personnel existe
        if (etudiant.getPersonnel() != null && etudiant.getPersonnel().getIdperso() != null) {
            Personnel personnel = personnelRepository.findById(etudiant.getPersonnel().getIdperso())
                    .orElseThrow(() -> new RuntimeException("Personnel non trouvé"));

            // Utiliser l'instance récupérée de la base de données
            etudiant.setPersonnel(personnel);
        }

        // Vérifier si le groupe existe
        if (etudiant.getGroupe() != null && etudiant.getGroupe().getId() != null) {
            Groupe groupe = groupeRepository.findById(etudiant.getGroupe().getId())
                    .orElseThrow(() -> new RuntimeException("Groupe non trouvé"));

            // Utiliser l'instance récupérée de la base de données
            etudiant.setGroupe(groupe);
        }

        // Si l'ID est fourni, vérifier qu'il n'existe pas déjà
        if (etudiant.getNumeroEtudiant() != null) {
            boolean exists = etudiantRepository.existsById(etudiant.getNumeroEtudiant());
            if (exists) {
                throw new RuntimeException("Un étudiant avec ce numéro existe déjà: " + etudiant.getNumeroEtudiant());
            }
        }

        // Définir la date de création si elle n'est pas déjà définie
        if (etudiant.getCreatedAt() == null) {
            etudiant.setCreatedAt(LocalDateTime.now());
        }

        return etudiantRepository.save(etudiant);
    }

    public List<Etudiant> getTousLesEtudiants() {
        return etudiantRepository.findAll();
    }







}