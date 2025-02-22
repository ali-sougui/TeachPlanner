package org.univ.projet_tutore.teachPlanner.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="departements", schema ="public")
public class Departement {
	@Id
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_departement")
	private Integer idDepartement;
	
	@Column(name = "nom_departement")
	private String nomDepartement;
	
	
	// Constructeur sans argument requis par Hibernate
    public Departement() {
    }
	
	
	public Departement(Integer idDepartement, String nomDepartement) {
		this.idDepartement = idDepartement;
		this.nomDepartement = nomDepartement;
	}


	public Integer getIdDepartement() {
		return idDepartement;
	}


	public void setIdDepartement(Integer idDepartement) {
		this.idDepartement = idDepartement;
	}


	public String getNomDepartement() {
		return nomDepartement;
	}


	public void setNomDepartement(String nomDepartement) {
		this.nomDepartement = nomDepartement;
	}
	
	

}
