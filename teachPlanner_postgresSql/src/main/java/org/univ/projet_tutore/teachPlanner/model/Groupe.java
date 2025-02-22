package org.univ.projet_tutore.teachPlanner.model;

import jakarta.persistence.*;

@Entity
@Table(name = "groupes")
public class Groupe {
    @Id
    private Integer num_groupe;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "type_cours_enum")
    private TypeCours type_cours;
    @Column(name = "code_matiere")
    private Integer codeMatiere;
    private String nom_groupe;

    public Groupe() {}
    public Groupe(Integer num_groupe,TypeCours type_cours, Integer codeMatiere,String nom_groupe) {
        this.num_groupe = num_groupe;
        this.type_cours = type_cours;
        this.codeMatiere = codeMatiere;
        this.nom_groupe = nom_groupe;
    }

    public Integer getNum_groupe() {
        return num_groupe;
    }
    public String getNom_groupe() {
        return nom_groupe;
    }
    public TypeCours getType_cours() {
        return type_cours;
    }
    public Integer getCodeMatiere() {
        return codeMatiere;
    }
    public void setNum_groupe(Integer num_groupe) {
        this.num_groupe = num_groupe;
    }
    public void setNom_groupe(String nom_groupe) {
        this.nom_groupe = nom_groupe;
    }
    public void setType_cours(TypeCours type_cours) {
        this.type_cours = type_cours;
    }
    public void setCodeMatiere(Integer codeMatiere) {
        this.codeMatiere = codeMatiere;
    }
}
