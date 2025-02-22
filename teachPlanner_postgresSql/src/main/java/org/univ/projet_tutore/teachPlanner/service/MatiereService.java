package org.univ.projet_tutore.teachPlanner.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.univ.projet_tutore.teachPlanner.model.Matiere;
import org.univ.projet_tutore.teachPlanner.repository.MatiereRepository;

@Service
public class MatiereService {
	@Autowired
	private MatiereRepository matiereRepository;
	
	//Recuperer toutes les matières
	public List<Matiere> getAllMatiere(){
	
		return matiereRepository.findAll();
	}
	
	//Recuperer une matière spécifique grâce à son Id.
	public Matiere getByIdMatiere(final Integer id) {
		Optional<Matiere> optionalMatiere = matiereRepository.findById(id);
		if(optionalMatiere.isPresent()) {
			return optionalMatiere.get();
		}else {
			throw new RuntimeException("Matiere non trouvé pour l'ID : "+id);
		}
	}
	
	//créer ou mettre à jour une matière.
	public Matiere updateOrCreateMatiere(Matiere matiere) {
		matiere.setNomMatiere(matiere.getNomMatiere().toUpperCase());
		return matiereRepository.save(matiere);
	}
	
	
	public String deleteAllMatiere() {
		long count = matiereRepository.count();
		
		if(count ==0) {
			return "Aucune matière à supprimer,la base de données est déjà vide";
		}
		matiereRepository.deleteAll();
		return  "Les "+count+" matières ont été supprimées";
	}
	
	//supprimer une matière spécifique grâce à son Id.
	public void deleteById(final Integer id) {
		Optional<Matiere> optionalMatiere = matiereRepository.findById(id);
		if(optionalMatiere.isPresent()) {
			matiereRepository.deleteById(id);
		}else {
			throw new RuntimeException("Impossible de supprimer la matière avec l'ID:"+id+"car elle n'existe pas");
		}
		
	}
}
