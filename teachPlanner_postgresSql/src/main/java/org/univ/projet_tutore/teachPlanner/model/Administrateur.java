package org.univ.projet_tutore.teachPlanner.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Administrateurs", schema = "public")
public class Administrateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_admin;

    @Column(nullable = false)
    private String nom_admin;

    @Column(nullable = false)
    private String prenom_admin;

    @OneToOne
    @JoinColumn(name = "idperso", nullable = false, referencedColumnName = "idperso")
    private Personnel personnel;

    // Getters et Setters
    public Integer getId_admin() {
        return id_admin;
    }

    public void setId_admin(Integer id_admin) {
        this.id_admin = id_admin;
    }

    public String getNom_admin() {
        return nom_admin;
    }

    public void setNom_admin(String nom_admin) {
        this.nom_admin = nom_admin;
    }

    public String getPrenom_admin() {
        return prenom_admin;
    }

    public void setPrenom_admin(String prenom_admin) {
        this.prenom_admin = prenom_admin;
    }

    public Personnel getPersonnel() {
        return personnel;
    }

    public void setPersonnel(Personnel personnel) {
        this.personnel = personnel;
    }
}
