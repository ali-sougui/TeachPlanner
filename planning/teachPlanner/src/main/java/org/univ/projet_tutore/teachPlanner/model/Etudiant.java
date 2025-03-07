package org.univ.projet_tutore.teachPlanner.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "etudiants")
public class Etudiant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "numero_etudiant")
    private Integer numeroEtudiant;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @OneToOne
    @JoinColumn(name = "idperso", unique = true)
    private Personnel personnel;

    @ManyToOne
    @JoinColumn(name = "groupe_id")
    private Groupe groupe;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Constructeurs
    public Etudiant() {}

    public Etudiant(String nom, String prenom) {
        this.nom = nom;
        this.prenom = prenom;
        this.createdAt = LocalDateTime.now();
    }

    // Getters et Setters
    public Integer getNumeroEtudiant() {
        return numeroEtudiant;
    }

    public void setNumeroEtudiant(Integer numeroEtudiant) {
        this.numeroEtudiant = numeroEtudiant;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public Personnel getPersonnel() {
        return personnel;
    }

    public void setPersonnel(Personnel personnel) {
        this.personnel = personnel;
    }

    public Groupe getGroupe() {
        return groupe;
    }

    public void setGroupe(Groupe groupe) {
        this.groupe = groupe;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}