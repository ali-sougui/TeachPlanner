package org.univ.projet_tutore.teachPlanner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.univ.projet_tutore.teachPlanner.dto.SeanceDTO;
import org.univ.projet_tutore.teachPlanner.model.Enseignant;
import org.univ.projet_tutore.teachPlanner.model.Seance;
import org.univ.projet_tutore.teachPlanner.model.Groupe;
import org.univ.projet_tutore.teachPlanner.dto.SeanceDetailsDTO;
import org.univ.projet_tutore.teachPlanner.dto.GroupeDTO;
import org.univ.projet_tutore.teachPlanner.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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
	private GroupeRepository groupeRepository;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<SeanceDetailsDTO> convertSeancesToDTO(List<Seance> seances) {
		return seances.stream()
				.map(this::convertToSeanceDetailsDTO)
				.collect(Collectors.toList());
	}

	public List<SeanceDetailsDTO> getAllSeances() {
		return seanceRepository.findAll().stream()
				.map(this::convertToSeanceDetailsDTO)
				.collect(Collectors.toList());
	}





	public List<SeanceDetailsDTO> getSeancesByGroupe(Integer groupeId) {
		String sql = """
            SELECT DISTINCT s.* 
            FROM seances s
            JOIN seances_groupes sg ON s.id_seance = sg.seance_id
            WHERE sg.groupe_id = ?
            ORDER BY s.date, s.heure_debut
        """;

		List<Seance> seances = jdbcTemplate.query(sql,
				(rs, rowNum) -> {
					Seance seance = new Seance();
					seance.setIdSeance(rs.getInt("id_seance"));
					seance.setHeureDebut(rs.getTime("heure_debut").toLocalTime());
					seance.setHeureFin(rs.getTime("heure_fin").toLocalTime());
					seance.setDate(rs.getDate("date").toLocalDate());
					seance.setNumeroEns(rs.getInt("numero_ens"));
					seance.setNumeroSalle(rs.getInt("numero_salle"));
					seance.setCodeMatiere(rs.getInt("code_matiere"));
					return seance;
				},
				groupeId
		);

		return convertSeancesToDTO(seances);
	}
	public List<SeanceDetailsDTO> getSeancesByEtudiant(Integer etudiantId) {
		String sql = """
            SELECT DISTINCT s.* 
            FROM seances s
            JOIN seances_groupes sg ON s.id_seance = sg.seance_id
            JOIN etudiants e ON e.groupe_id = sg.groupe_id
            WHERE e.numero_etudiant = ?
            ORDER BY s.date, s.heure_debut
        """;

		List<Seance> seances = jdbcTemplate.query(sql,
				(rs, rowNum) -> {
					Seance seance = new Seance();
					seance.setIdSeance(rs.getInt("id_seance"));
					seance.setHeureDebut(rs.getTime("heure_debut").toLocalTime());
					seance.setHeureFin(rs.getTime("heure_fin").toLocalTime());
					seance.setDate(rs.getDate("date").toLocalDate());
					seance.setNumeroEns(rs.getInt("numero_ens"));
					seance.setNumeroSalle(rs.getInt("numero_salle"));
					seance.setCodeMatiere(rs.getInt("code_matiere"));
					return seance;
				},
				etudiantId
		);

		return seances.stream()
				.map(this::convertToSeanceDetailsDTO)
				.collect(Collectors.toList());
	}

	public List<SeanceDetailsDTO> getSeancesByEnseignant(Integer enseignantId) {
		return seanceRepository.findByNumeroEns(enseignantId).stream()
				.map(this::convertToSeanceDetailsDTO)
				.collect(Collectors.toList());
	}


	public Map<String, Long> getDashboardStats(Integer enseignantId) {
		Map<String, Long> stats = new HashMap<>();

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
			stats.put("totalSeances", 0L);
			stats.put("totalMatieres", 0L);
			stats.put("totalSalles", 0L);
			stats.put("totalHeures", 0L);
			return stats;
		}
	}

	@Transactional
	public Seance addSeance(Seance seance) {
		logger.info("Ajout d'une nouvelle séance: {}", seance);
		return seanceRepository.save(seance);
	}

	@Transactional
	public void deleteSeance(Integer id) {
		seanceRepository.deleteById(id);
	}


	@Transactional
	public Seance updateSeance(Integer id, SeanceDTO dto) {
		Seance seance = seanceRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Séance non trouvée"));

		seance.setHeureDebut(LocalTime.parse(dto.getHeureDebut()));
		seance.setHeureFin(LocalTime.parse(dto.getHeureFin()));
		seance.setDate(LocalDate.parse(dto.getDate()));
		seance.setNumeroEns(dto.getNumeroEns());
		seance.setNumeroSalle(dto.getNumeroSalle());
		seance.setCodeMatiere(dto.getCodeMatiere());

		if (dto.getGroupeIds() != null) {
			Set<Groupe> groupes = dto.getGroupeIds().stream()
					.map(ide -> groupeRepository.findById(ide)
							.orElseThrow(() -> new RuntimeException("Groupe non trouvé: " + ide)))
					.collect(Collectors.toSet());
			seance.setGroupes(groupes);
		}

		return seanceRepository.save(seance);
	}

	@Transactional
	public void addGroupeToSeance(Integer seanceId, Integer groupeId) {
		Seance seance = seanceRepository.findById(seanceId)
				.orElseThrow(() -> new RuntimeException("Séance non trouvée"));

		Groupe groupe = groupeRepository.findById(groupeId)
				.orElseThrow(() -> new RuntimeException("Groupe non trouvé"));

		seance.getGroupes().add(groupe);
		seanceRepository.save(seance);
	}

	public Seance createSeance(SeanceDTO dto) {
		Seance seance = new Seance();
		seance.setHeureDebut(LocalTime.parse(dto.getHeureDebut()));
		seance.setHeureFin(LocalTime.parse(dto.getHeureFin()));
		seance.setDate(LocalDate.parse(dto.getDate()));
		seance.setNumeroEns(dto.getNumeroEns());
		seance.setNumeroSalle(dto.getNumeroSalle());
		seance.setCodeMatiere(dto.getCodeMatiere());

		if (dto.getGroupeIds() != null && !dto.getGroupeIds().isEmpty()) {
			Set<Groupe> groupes = dto.getGroupeIds().stream()
					.map(id -> groupeRepository.findById(id)
							.orElseThrow(() -> new RuntimeException("Groupe non trouvé: " + id)))
					.collect(Collectors.toSet());
			seance.setGroupes(groupes);
		}

		return seanceRepository.save(seance);
	}
	@Transactional
	public void removeGroupeFromSeance(Integer seanceId, Integer groupeId) {
		Seance seance = seanceRepository.findById(seanceId)
				.orElseThrow(() -> new RuntimeException("Séance non trouvée"));

		seance.getGroupes().removeIf(groupe -> groupe.getId().equals(groupeId));
		seanceRepository.save(seance);
	}

	public int getNombreDeSemaines() {
		Set<Integer> semaines = seanceRepository.findAll().stream()
				.map(seance -> {
					LocalDate date = seance.getDate();
					return date.get(WeekFields.of(Locale.FRANCE).weekOfYear());
				})
				.collect(Collectors.toSet());

		return semaines.size();
	}
	public SeanceDetailsDTO convertToSeanceDetailsDTO(Seance seance) {
		SeanceDetailsDTO dto = new SeanceDetailsDTO();
		dto.setIdSeance(seance.getIdSeance());
		dto.setHeureDebut(seance.getHeureDebut().toString());
		dto.setHeureFin(seance.getHeureFin().toString());
		dto.setDate(seance.getDate().toString());
		dto.setNumeroEns(seance.getNumeroEns());
		dto.setNumeroSalle(seance.getNumeroSalle());
		dto.setCodeMatiere(seance.getCodeMatiere());

		// Récupérer les informations de l'enseignant
		String nomEnseignant = jdbcTemplate.queryForObject(
				"SELECT CONCAT(nom_ens, ' ', prenom_ens) FROM enseignants WHERE numero_ens = ?",
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

		// Convertir les groupes
		Set<GroupeDTO> groupeDTOs = seance.getGroupes().stream()
				.map(groupe -> {
					GroupeDTO groupeDTO = new GroupeDTO();
					groupeDTO.setId(groupe.getId());
					groupeDTO.setNom(groupe.getNom());
					groupeDTO.setAnneeScolaire(groupe.getAnneeScolaire());
					return groupeDTO;
				})
				.collect(Collectors.toSet());
		dto.setGroupes(groupeDTOs);

		return dto;
	}


	public List<SeanceDetailsDTO> getAllSeancesWithDetailsByEnseignant(int numeroEnseignant) {
		return seanceRepository.findAll().stream()
				.filter(seance -> seance.getNumeroEns() == numeroEnseignant)
				.map(this::convertToSeanceDetailsDTO)
				.collect(Collectors.toList());
	}


	/**
	 * Récupère la date la plus ancienne parmi toutes les séances.
	 *
	 * @return La date la plus ancienne (LocalDate). Retourne null si aucune séance n'existe.
	 */
	public LocalDate getPremiereDate() {
		return seanceRepository.findAll().stream()
				.map(Seance::getDate)
				.min(LocalDate::compareTo)
				.orElse(null);
	}
}