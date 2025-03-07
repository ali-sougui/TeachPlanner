package org.univ.projet_tutore.teachPlanner.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "seances")
public class Seance {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_seance")
	private Integer idSeance;

	@Column(name = "heure_debut")
	private LocalTime heureDebut;

	@Column(name = "heure_fin")
	private LocalTime heureFin;

	@Column(name = "date")
	private LocalDate date;

	@Column(name = "numero_ens")
	private Integer numeroEns;

	@Column(name = "numero_salle")
	private Integer numeroSalle;

	@Column(name = "code_matiere")
	private Integer codeMatiere;

	@ManyToMany
	@JoinTable(
			name = "seances_groupes",
			joinColumns = @JoinColumn(name = "seance_id"),
			inverseJoinColumns = @JoinColumn(name = "groupe_id")
	)
	@JsonIgnoreProperties("seances")
	private Set<Groupe> groupes = new HashSet<>();

	// Constructeurs
	public Seance() {}

	// Getters et Setters
	public Integer getIdSeance() {
		return idSeance;
	}

	public void setIdSeance(Integer idSeance) {
		this.idSeance = idSeance;
	}

	public LocalTime getHeureDebut() {
		return heureDebut;
	}

	public void setHeureDebut(LocalTime heureDebut) {
		this.heureDebut = heureDebut;
	}

	public LocalTime getHeureFin() {
		return heureFin;
	}

	public void setHeureFin(LocalTime heureFin) {
		this.heureFin = heureFin;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
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

	public Set<Groupe> getGroupes() {
		return groupes;
	}

	public void setGroupes(Set<Groupe> groupes) {
		this.groupes = groupes;
	}

	// Méthodes utilitaires pour la gestion des groupes
	public void addGroupe(Groupe groupe) {
		this.groupes.add(groupe);
		groupe.getSeances().add(this);
	}

	public void removeGroupe(Groupe groupe) {
		this.groupes.remove(groupe);
		groupe.getSeances().remove(this);
	}
}