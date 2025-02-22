package org.univ.projet_tutore.teachPlanner.dto;

import lombok.Data;

@Data
public class SeanceDetailsDTO {
    private Integer idSeance;
    private String heureDebut;
    private String heureFin;
    private String date;
    private Integer numeroEns;
    private String nomEnseignant;
    private Integer numeroSalle;
    private String nomSalle;
    private Integer codeMatiere;
    private String nomMatiere;

    // Getters et Setters
    public Integer getIdSeance() {
        return idSeance;
    }

    public void setIdSeance(Integer idSeance) {
        this.idSeance = idSeance;
    }

    public String getHeureDebut() {
        return heureDebut;
    }

    public void setHeureDebut(String heureDebut) {
        this.heureDebut = heureDebut;
    }

    public String getHeureFin() {
        return heureFin;
    }

    public void setHeureFin(String heureFin) {
        this.heureFin = heureFin;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getNumeroEns() {
        return numeroEns;
    }

    public void setNumeroEns(Integer numeroEns) {
        this.numeroEns = numeroEns;
    }

    public String getNomEnseignant() {
        return nomEnseignant;
    }

    public void setNomEnseignant(String nomEnseignant) {
        this.nomEnseignant = nomEnseignant;
    }

    public Integer getNumeroSalle() {
        return numeroSalle;
    }

    public void setNumeroSalle(Integer numeroSalle) {
        this.numeroSalle = numeroSalle;
    }

    public String getNomSalle() {
        return nomSalle;
    }

    public void setNomSalle(String nomSalle) {
        this.nomSalle = nomSalle;
    }

    public Integer getCodeMatiere() {
        return codeMatiere;
    }

    public void setCodeMatiere(Integer codeMatiere) {
        this.codeMatiere = codeMatiere;
    }

    public String getNomMatiere() {
        return nomMatiere;
    }

    public void setNomMatiere(String nomMatiere) {
        this.nomMatiere = nomMatiere;
    }
}