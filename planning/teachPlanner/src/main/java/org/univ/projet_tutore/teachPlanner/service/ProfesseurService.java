package org.univ.projet_tutore.teachPlanner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.univ.projet_tutore.teachPlanner.dto.SeanceDetailsDTO;
import org.univ.projet_tutore.teachPlanner.model.Seance;
import org.univ.projet_tutore.teachPlanner.repository.EnseignantRepository;
import org.univ.projet_tutore.teachPlanner.repository.MatiereRepository;
import org.univ.projet_tutore.teachPlanner.repository.SalleRepository;
import org.univ.projet_tutore.teachPlanner.repository.SeanceRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProfesseurService {
    @Autowired
    private SeanceRepository seanceRepository;
    @Autowired
    private EnseignantRepository enseignantRepository;
    @Autowired
    private MatiereRepository matiereRepository;
    @Autowired
    private SalleRepository salleRepository;

    public List<SeanceDetailsDTO> getEmploiDuTemps(Integer enseignantId) {
        List<SeanceDetailsDTO> emploiDuTemps = seanceRepository.findByNumeroEns(enseignantId)
                .stream()
                .map(this::convertToSeanceDetailsDTO)
                .collect(Collectors.toList());

        System.out.println("Nombre de séances trouvées pour l'enseignant " + enseignantId + " : " + emploiDuTemps.size());
        return emploiDuTemps;
    }


    private SeanceDetailsDTO convertToSeanceDetailsDTO(Seance seance) {
        SeanceDetailsDTO dto = new SeanceDetailsDTO();
        dto.setIdSeance(seance.getIdSeance());
        dto.setHeureDebut(seance.getHeureDebut().toString());
        dto.setHeureFin(seance.getHeureFin().toString());
        dto.setDate(seance.getDate().toString());
        dto.setNumeroEns(seance.getNumeroEns());
        dto.setNumeroSalle(seance.getNumeroSalle());
        dto.setCodeMatiere(seance.getCodeMatiere());

        enseignantRepository.findById(seance.getNumeroEns().longValue()).ifPresent(ens ->
                dto.setNomEnseignant(ens.getNomEns() + " " + ens.getPrenomEns()));

        matiereRepository.findById(seance.getCodeMatiere()).ifPresent(mat ->
                dto.setNomMatiere(mat.getNomMatiere()));

        salleRepository.findById(seance.getNumeroSalle()).ifPresent(salle ->
                dto.setNomSalle(salle.getNomSalle()));

        return dto;
    }

}
