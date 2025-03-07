package org.univ.projet_tutore.teachPlanner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "groupes")
public class Groupe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // ✅ Assure-toi que l’ID est auto-généré
    private Integer id;


    @Column(nullable = false)
    private String nom;

    @Column(name = "annee_scolaire", nullable = false)
    private String anneeScolaire;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "groupe")
    @JsonIgnoreProperties("groupe")
    private Set<Etudiant> etudiants = new HashSet<>();

    @ManyToMany(mappedBy = "groupes")
    @JsonIgnoreProperties("groupes")
    private Set<Seance> seances = new HashSet<>();

    // Constructeurs
    public Groupe() {}

    public Groupe(String nom, String anneeScolaire) {
        this.nom = nom;
        this.anneeScolaire = anneeScolaire;
        this.createdAt = LocalDateTime.now();
    }

    // Getters et Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getAnneeScolaire() {
        return anneeScolaire;
    }

    public void setAnneeScolaire(String anneeScolaire) {
        this.anneeScolaire = anneeScolaire;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<Etudiant> getEtudiants() {
        return etudiants;
    }

    public void setEtudiants(Set<Etudiant> etudiants) {
        this.etudiants = etudiants;
    }

    public Set<Seance> getSeances() {
        return seances;
    }

    public void setSeances(Set<Seance> seances) {
        this.seances = seances;
    }
}