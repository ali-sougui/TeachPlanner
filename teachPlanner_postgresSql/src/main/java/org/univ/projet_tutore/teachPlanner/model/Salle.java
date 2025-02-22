package org.univ.projet_tutore.teachPlanner.model;

import jakarta.persistence.*;

@Entity
@Table(name = "salles")
public class Salle {


    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)  // La génération automatique fonctionne avec un Long ou Integer
    @Column(name = "numero_salle")
    private int numSalle;  // Changer de String à Long ou Integer pour être compatible avec l'auto-incrémentation

    @Column(name = "nom_salle")
    private String nomSalle;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_salle")
    //@Convert(converter = TypeSalleConverter.class)
    private TypeSalle typeSalle;

    public Salle(int numSalle, String nomSalle, TypeSalle typeSalle) {
        this.numSalle = numSalle;
        this.nomSalle = nomSalle;
        this.typeSalle = typeSalle;
    }

    public Salle() {}

    public int getNumSalle() {  // Modifier le type de retour en Long
        return numSalle;
    }

    public String getNomSalle() {
        return nomSalle;
    }

    public TypeSalle getTypeSalle() {
        return typeSalle;
    }
    public void setNomSalle(String nomSalle) {
        this.nomSalle = nomSalle;
    }
    public void setTypeSalle(TypeSalle typeSalle) {
        this.typeSalle = typeSalle;
    }
    public void setNumSalle(int numSalle) {
        this.numSalle = numSalle;
    }
}
