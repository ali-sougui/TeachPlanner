package org.univ.projet_tutore.teachPlanner.dto;

import java.util.Set;

public class SeanceDTO {
    private String heureDebut;
    private String heureFin;
    private String date;
    private Integer numeroEns;
    private Integer numeroSalle;
    private Integer codeMatiere;
    private Set<Integer> groupeIds;

    // Constructeur par défaut
    public SeanceDTO() {}

    // Getters et Setters
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

    public Integer getNumeroSalle() {
        return numeroSalle;
    }

    public void setNumeroSalle(Integer numeroSalle) {
        this.numeroSalle = numeroSalle;
    }

    public Integer getCodeMatiere() {
        return codeMatiere;
    }

    public void setCodeMatiere(Integer codeMatiere) {
        this.codeMatiere = codeMatiere;
    }

    public Set<Integer> getGroupeIds() {
        return groupeIds;
    }

    public void setGroupeIds(Set<Integer> groupeIds) {
        this.groupeIds = groupeIds;
    }
}