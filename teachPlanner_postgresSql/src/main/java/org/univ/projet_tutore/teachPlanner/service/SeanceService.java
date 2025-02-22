package org.univ.projet_tutore.teachPlanner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.univ.projet_tutore.teachPlanner.model.Enseignant;
import org.univ.projet_tutore.teachPlanner.model.Seance;
import org.univ.projet_tutore.teachPlanner.dto.SeanceDetailsDTO;
import org.univ.projet_tutore.teachPlanner.repository.SeanceRepository;
import org.univ.projet_tutore.teachPlanner.repository.EnseignantRepository;
import org.univ.projet_tutore.teachPlanner.repository.MatiereRepository;
import org.univ.projet_tutore.teachPlanner.repository.SalleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SeanceService {
	private static final Logger logger = LoggerFactory.getLogger(SeanceService.class);

	@Autowired
	private SeanceRepository seanceRepository;
	@Autowired
	private JdbcTemplate jdbcTemplate;



	public List<SeanceDetailsDTO> convertSeancesToDTO(List<Seance> seances) {
		return seances.stream()
				.map(this::convertToSeanceDetailsDTO)
				.collect(Collectors.toList());
	}

	public List<SeanceDetailsDTO> getAllSeances() {
		List<Seance> seances = seanceRepository.findAll();
		return seances.stream()
				.map(this::convertToSeanceDetailsDTO)
				.collect(Collectors.toList());
	}

	public List<SeanceDetailsDTO> getSeancesByEnseignant(Integer enseignantId) {
		List<Seance> seances = seanceRepository.findByNumeroEns(enseignantId);
		return convertSeancesToDTO(seances);
	}

	private SeanceDetailsDTO convertToSeanceDetailsDTO(Seance seance) {
		SeanceDetailsDTO dto = new SeanceDetailsDTO();

		dto.setIdSeance(seance.getIdSeance().intValue());
		dto.setHeureDebut(seance.getHeureDebut().toString());
		dto.setHeureFin(seance.getHeureFin().toString());
		dto.setDate(seance.getDate().toString());
		dto.setNumeroEns(seance.getNumeroEns());
		dto.setNumeroSalle(seance.getNumeroSalle());
		dto.setCodeMatiere(seance.getCodeMatiere());

		// Récupérer le nom de l'enseignant
		String nomEnseignant = jdbcTemplate.queryForObject(
				"SELECT nom_ens || ' ' || prenom_ens FROM personnels p " +
						"JOIN enseignants e ON e.idperso = p.idperso " +
						"WHERE e.numero_ens = ?",
				String.class,
				seance.getNumeroEns()
		);
		dto.setNomEnseignant(nomEnseignant);

		// Récupérer le nom de la salle
		String nomSalle = jdbcTemplate.queryForObject(
				"SELECT nom_salle FROM salles WHERE numero_salle = ?",
				String.class,
				seance.getNumeroSalle()
		);
		dto.setNomSalle(nomSalle);

		// Récupérer le nom de la matière
		String nomMatiere = jdbcTemplate.queryForObject(
				"SELECT nom_matiere FROM matieres WHERE code_matiere = ?",
				String.class,
				seance.getCodeMatiere()
		);
		dto.setNomMatiere(nomMatiere);

		return dto;
	}
	public Map<String, Long> getDashboardStats(Integer enseignantId) {
		Map<String, Long> stats = new HashMap<>();

		// Requête SQL modifiée pour prendre en compte toutes les séances
		String sql = """
            SELECT 
                COUNT(DISTINCT s.id_seance) as total_seances,
                COUNT(DISTINCT s.code_matiere) as total_matieres,
                COUNT(DISTINCT s.numero_salle) as total_salles,
                CAST(SUM(
                    EXTRACT(EPOCH FROM (s.heure_fin::time - s.heure_debut::time))/3600
                ) AS BIGINT) as total_heures
            FROM seances s
            WHERE 1=1
            """ + (enseignantId != null ? " AND s.numero_ens = ?" : "");

		Object[] params = enseignantId != null ?
				new Object[]{enseignantId} :
				new Object[]{};

		try {
			return jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
				Map<String, Long> result = new HashMap<>();
				result.put("totalSeances", rs.getLong("total_seances"));
				result.put("totalMatieres", rs.getLong("total_matieres"));
				result.put("totalSalles", rs.getLong("total_salles"));
				result.put("totalHeures", rs.getLong("total_heures"));
				return result;
			});
		} catch (Exception e) {
			e.printStackTrace();
			// En cas d'erreur, retourner des statistiques à 0
			stats.put("totalSeances", 0L);
			stats.put("totalMatieres", 0L);
			stats.put("totalSalles", 0L);
			stats.put("totalHeures", 0L);
			return stats;
		}
	}

	// Méthodes pour les statistiques
	public long getTotalSeancesWeek() {
		LocalDate today = LocalDate.now();
		LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

		return jdbcTemplate.queryForObject(
				"SELECT COUNT(*) FROM seances WHERE date BETWEEN ? AND ?",
				Long.class,
				startOfWeek,
				endOfWeek
		);
	}

	public long getTotalHeuresWeek() {
		LocalDate today = LocalDate.now();
		LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

		return jdbcTemplate.queryForObject(
				"SELECT COALESCE(SUM(EXTRACT(EPOCH FROM (heure_fin - heure_debut))/3600), 0) " +
						"FROM seances WHERE date BETWEEN ? AND ?",
				Long.class,
				startOfWeek,
				endOfWeek
		);
	}

	public long getTotalMatieresWeek() {
		LocalDate today = LocalDate.now();
		LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

		return jdbcTemplate.queryForObject(
				"SELECT COUNT(DISTINCT code_matiere) FROM seances WHERE date BETWEEN ? AND ?",
				Long.class,
				startOfWeek,
				endOfWeek
		);
	}

	public long getTotalSallesWeek() {
		LocalDate today = LocalDate.now();
		LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
		LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

		return jdbcTemplate.queryForObject(
				"SELECT COUNT(DISTINCT numero_salle) FROM seances WHERE date BETWEEN ? AND ?",
				Long.class,
				startOfWeek,
				endOfWeek
		);
	}

	@Transactional
	public Seance addSeance(Seance seance) {
		System.out.println("Service: " + seance);
		System.out.println("Heure début: " + seance.getHeureDebut());
		return seanceRepository.save(seance);
	}

	public void deleteSeance(Integer id) {
		seanceRepository.deleteById(id);
	}

	@Transactional
	public Seance updateSeance(Seance seanceModif) {
		Seance existingSeance = seanceRepository.findById(seanceModif.getIdSeance()).orElse(null);
		if (existingSeance != null) {
			existingSeance.setHeureDebut(seanceModif.getHeureDebut());
			existingSeance.setHeureFin(seanceModif.getHeureFin());
			existingSeance.setDate(seanceModif.getDate());
			existingSeance.setNumeroSalle(seanceModif.getNumeroSalle());
			existingSeance.setCodeMatiere(seanceModif.getCodeMatiere());
			existingSeance.setNumeroEns(seanceModif.getNumeroEns());
			return seanceRepository.save(existingSeance);
		}
		return null;
	}
	public int getNombreDeSemaines() {
		Set<Integer> semaines = seanceRepository.findAll().stream()
				.map(seance -> {
					LocalDate date = seance.getDate();
//					System.out.println("le nombre de semaine : "+date.get(WeekFields.of(Locale.FRANCE).weekOfYear()));
					return date.get(WeekFields.of(Locale.FRANCE).weekOfYear());
				})
				.collect(Collectors.toSet());

		System.out.println(semaines.size());

		return semaines.size();
	}


}