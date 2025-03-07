package org.univ.projet_tutore.teachPlanner.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name ="matieres",schema = "public")
public class Matiere {
	@Id
	@Column(name = "code_matiere")
	private Integer codeMatiere;
	
	@Column(name = "nom_matiere")
	private String nomMatiere;
	
	///Constructeur sans argument requis par Hibernate 
	public Matiere() {
		
	}
	
	public Matiere(Integer codeMatiere, String nomMatiere) {
		this.codeMatiere = codeMatiere;
		this.nomMatiere = nomMatiere;
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
