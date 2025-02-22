package org.univ.projet_tutore.teachPlanner.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter

@Entity
@Table(name = "seances")
public class Seance {
// heure_debut, heure_fin, date, numero_ens, numero_salle, code_matiere^
	
	
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
	private Integer  numeroSalle;
    
    
    @Column(name = "code_matiere")
	private Integer codeMatiere;

	public Seance(){}
	public Seance(Integer idSeance,LocalTime heureDebut,LocalTime heureFin,LocalDate date,Integer numeroEns,Integer numeroSalle,Integer codeMatiere) {
		this.idSeance = idSeance;
		this.heureDebut = heureDebut;
		this.heureFin = heureFin;
		this.date = date;
		this.numeroEns = numeroEns;
		this.numeroSalle = numeroSalle;
		this.codeMatiere = codeMatiere;
	}
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

	// Setter
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

}
