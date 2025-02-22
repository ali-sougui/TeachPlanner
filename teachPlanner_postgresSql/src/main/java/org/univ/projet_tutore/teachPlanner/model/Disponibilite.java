package org.univ.projet_tutore.teachPlanner.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "disponibilites")
public class Disponibilite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_ens", nullable = false)
    private Integer numeroEns;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JourSemaine jour;

    public enum JourSemaine {
        LUNDI, MARDI, MERCREDI, JEUDI, VENDREDI
    }

    // Constructeurs
    public Disponibilite() {}

    public Disponibilite(Integer numeroEns, JourSemaine jour) {
        this.numeroEns = numeroEns;
        this.jour = jour;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumeroEns() {
        return numeroEns;
    }

    public void setNumeroEns(Integer numeroEns) {
        this.numeroEns = numeroEns;
    }

    public JourSemaine getJour() {
        return jour;
    }

    public void setJour(JourSemaine jour) {
        this.jour = jour;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Disponibilite that = (Disponibilite) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(numeroEns, that.numeroEns) &&
                jour == that.jour;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numeroEns, jour);
    }
}