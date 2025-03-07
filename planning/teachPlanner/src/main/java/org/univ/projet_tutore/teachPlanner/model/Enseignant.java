package org.univ.projet_tutore.teachPlanner.model;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


import jakarta.persistence.*;

@Entity
@Table(name = "enseignants")
public class Enseignant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "numero_ens")
    private Integer numeroEns;

    @Column(nullable = false)
    private String nomEns;

    @Column(nullable = false)
    private String prenomEns;

    @OneToOne
    @JoinColumn(name = "idperso", unique = true)
    private Personnel personnel;

    // Getters et Setters
    public Integer getNumeroEns() {
        return numeroEns;
    }

    public void setNumeroEns(Integer numeroEns) {
        this.numeroEns = numeroEns;
    }

    public String getNomEns() {
        return nomEns;
    }

    public void setNomEns(String nomEns) {
        this.nomEns = nomEns;
    }

    public String getPrenomEns() {
        return prenomEns;
    }

    public void setPrenomEns(String prenomEns) {
        this.prenomEns = prenomEns;
    }

    public Personnel getPersonnel() {
        return personnel;
    }

    public void setPersonnel(Personnel personnel) {
        this.personnel = personnel;
    }
}
