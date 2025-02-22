package org.univ.projet_tutore.teachPlanner.model;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "\"Enseignants\"", schema ="public")

public class Enseignant {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY) 
    @Column(name = "numero_ens")
    private Integer id;

    @Column(name = "nom_ens", length = 50)
    private String nom;

    @Column(name = "prenom_ens", length = 50)
    private String prenom;

    @Column(name = "idperso")
    private Integer idperso;

    // Constructeurs, getters, et setters
    public Enseignant() {}
    public Enseignant(Integer id, String nom, String prenom, Integer idperso) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.idperso = idperso;
    }

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
    public String getPrenom() {
        return prenom;
    }
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    public Integer getIdperso() {
        return idperso;
    }
    public void setIdperso(Integer idperso) {
        this.idperso = idperso;
    }
}
