package org.univ.projet_tutore.teachPlanner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; 
import org.springframework.transaction.annotation.Transactional; 
import org.univ.projet_tutore.teachPlanner.model.Enseignant;
import org.univ.projet_tutore.teachPlanner.repository.EnseignantRepository;

import java.util.List;

@Service 
public class EnseignantService {
	
	@Autowired 
	private EnseignantRepository enseignantRepository;
	
	public List<Enseignant> getAllEnseignants() { 
		return enseignantRepository.findAll(); 
	} 
	
	@Transactional(readOnly = false) 
	public Enseignant addEnseignant(Enseignant enseignant) { 
		return enseignantRepository.save(enseignant); 
	} 
	
	@Transactional 
	public void deleteEnseignantById(Long id) {
		enseignantRepository.deleteById(id); 
	} 
	
	@Transactional 
	public Enseignant updateEnseignant(Enseignant enseignant) { 
		return enseignantRepository.save(enseignant); // Met à jour si l'ID existe 
	} 
	
	public Enseignant getEnseignantById(Long id) {
		return enseignantRepository.findById(id).orElse(null); 
	} 
	public List<Enseignant> searchEnseignants(String searchTerm) {
	    return enseignantRepository.findByNomContainingOrPrenomContaining(searchTerm, searchTerm);
	}

	public Integer getNumeroEnsByIdPerso(Integer idperso) {
		try {
			return enseignantRepository.findByIdperso(idperso)
					.map(Enseignant::getId)
					.orElse(null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


}