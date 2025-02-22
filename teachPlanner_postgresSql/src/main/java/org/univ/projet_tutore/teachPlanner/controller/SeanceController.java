package org.univ.projet_tutore.teachPlanner.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.univ.projet_tutore.teachPlanner.model.Seance;
import org.univ.projet_tutore.teachPlanner.service.SeanceService;
import org.univ.projet_tutore.teachPlanner.dto.SeanceDTO;
import org.univ.projet_tutore.teachPlanner.dto.SeanceDetailsDTO;

@Controller
@CrossOrigin(origins = "*")
public class SeanceController {

	@Autowired
	private SeanceService seanceService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@GetMapping("/seances")
	public String showCalendar() {
		return "pages/seance";
	}

	@GetMapping("/api/seances")
	@ResponseBody
	public ResponseEntity<?> getAllSeances(HttpSession session) {
		try {
			if (session.getAttribute("userLoggedIn") == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(Map.of("error", "Non autorisé"));
			}

			Integer idperso = (Integer) session.getAttribute("userId");

			String nomComplet = jdbcTemplate.queryForObject(
					"SELECT CONCAT(p.nom_ens, ' ', p.prenom_ens) FROM enseignants p WHERE p.idperso = ?",
					new Object[]{idperso},
					String.class
			);

			List<SeanceDetailsDTO> seances;
			if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
				Integer numeroEns = jdbcTemplate.queryForObject(
						"SELECT numero_ens FROM enseignants WHERE idperso = ?",
						new Object[]{idperso},
						Integer.class
				);
				seances = seanceService.getSeancesByEnseignant(numeroEns);
			} else {
				seances = seanceService.getAllSeances();
			}

			Map<String, Object> response = new HashMap<>();
			response.put("seances", seances);
			response.put("nomComplet", nomComplet);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Erreur lors de la récupération des séances: " + e.getMessage()));
		}
	}

	@PostMapping("/api/seances")
	@ResponseBody
	public ResponseEntity<?> createSeance(@RequestBody SeanceDTO seanceDTO) {
		try {
			// Vérifier la disponibilité de la salle
			boolean salleDisponible = checkSalleDisponible(
					seanceDTO.getNumeroSalle(),
					seanceDTO.getDate(),
					seanceDTO.getHeureDebut(),
					seanceDTO.getHeureFin(),
					null
			);

			if (!salleDisponible) {
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body(Map.of("error", "La salle n'est pas disponible pour ce créneau horaire"));
			}

			// Vérifier la disponibilité de l'enseignant
			boolean enseignantDisponible = checkEnseignantDisponible(
					seanceDTO.getNumeroEns(),
					seanceDTO.getDate(),
					seanceDTO.getHeureDebut(),
					seanceDTO.getHeureFin(),
					null
			);

			if (!enseignantDisponible) {
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body(Map.of("error", "L'enseignant n'est pas disponible pour ce créneau horaire"));
			}

			Seance seance = new Seance();
			seance.setHeureDebut(LocalTime.parse(seanceDTO.getHeureDebut()));
			seance.setHeureFin(LocalTime.parse(seanceDTO.getHeureFin()));
			seance.setDate(LocalDate.parse(seanceDTO.getDate()));
			seance.setNumeroEns(seanceDTO.getNumeroEns());
			seance.setNumeroSalle(seanceDTO.getNumeroSalle());
			seance.setCodeMatiere(seanceDTO.getCodeMatiere());

			Seance savedSeance = seanceService.addSeance(seance);
			return ResponseEntity.ok(savedSeance);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Erreur lors de la création de la séance: " + e.getMessage()));
		}
	}

	@PutMapping("/api/seances/{id}")
	@ResponseBody
	public ResponseEntity<?> updateSeance(@PathVariable Integer id, @RequestBody SeanceDTO seanceDTO) {
		try {
			// Vérifier la disponibilité de la salle
			boolean salleDisponible = checkSalleDisponible(
					seanceDTO.getNumeroSalle(),
					seanceDTO.getDate(),
					seanceDTO.getHeureDebut(),
					seanceDTO.getHeureFin(),
					id
			);

			if (!salleDisponible) {
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body(Map.of("error", "La salle n'est pas disponible pour ce créneau horaire"));
			}

			// Vérifier la disponibilité de l'enseignant
			boolean enseignantDisponible = checkEnseignantDisponible(
					seanceDTO.getNumeroEns(),
					seanceDTO.getDate(),
					seanceDTO.getHeureDebut(),
					seanceDTO.getHeureFin(),
					id
			);

			if (!enseignantDisponible) {
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body(Map.of("error", "L'enseignant n'est pas disponible pour ce créneau horaire"));
			}

			Seance seance = new Seance();
			seance.setIdSeance(id);
			seance.setHeureDebut(LocalTime.parse(seanceDTO.getHeureDebut()));
			seance.setHeureFin(LocalTime.parse(seanceDTO.getHeureFin()));
			seance.setDate(LocalDate.parse(seanceDTO.getDate()));
			seance.setNumeroEns(seanceDTO.getNumeroEns());
			seance.setNumeroSalle(seanceDTO.getNumeroSalle());
			seance.setCodeMatiere(seanceDTO.getCodeMatiere());

			Seance updatedSeance = seanceService.updateSeance(seance);
			return ResponseEntity.ok(updatedSeance);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Erreur lors de la mise à jour de la séance: " + e.getMessage()));
		}
	}

	@DeleteMapping("/api/seances/{id}")
	@ResponseBody
	public ResponseEntity<?> deleteSeance(@PathVariable Integer id) {
		try {
			seanceService.deleteSeance(id);
			return ResponseEntity.ok(Map.of("message", "Séance supprimée avec succès"));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Erreur lors de la suppression de la séance: " + e.getMessage()));
		}
	}

	@GetMapping("/api/dashboard-stats")
	@ResponseBody
	public ResponseEntity<?> getDashboardStats(HttpSession session) {
		try {
			if (session.getAttribute("userLoggedIn") == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(Map.of("error", "Non autorisé"));
			}

			Integer enseignantId = null;
			if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
				Integer idperso = (Integer) session.getAttribute("userId");
				if (idperso != null) {
					try {
						enseignantId = jdbcTemplate.queryForObject(
								"SELECT numero_ens FROM enseignants WHERE idperso = ?",
								Integer.class,
								idperso
						);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			Map<String, Long> stats = seanceService.getDashboardStats(enseignantId);
			return ResponseEntity.ok(stats);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Erreur lors de la récupération des statistiques: " + e.getMessage()));
		}
	}

	private boolean checkSalleDisponible(Integer numeroSalle, String date, String heureDebut, String heureFin, Integer seanceId) {
		String sql = """
            SELECT COUNT(*) FROM seances 
            WHERE numero_salle = ? 
            AND date = ?::date 
            AND (
                (heure_debut <= ?::time AND heure_fin > ?::time) 
                OR (heure_debut < ?::time AND heure_fin >= ?::time) 
                OR (heure_debut >= ?::time AND heure_fin <= ?::time)
            )
        """;

		if (seanceId != null) {
			sql += " AND id_seance != ?";
		}

		Object[] params = seanceId != null ?
				new Object[]{numeroSalle, date, heureFin, heureDebut, heureFin, heureDebut, heureDebut, heureFin, seanceId} :
				new Object[]{numeroSalle, date, heureFin, heureDebut, heureFin, heureDebut, heureDebut, heureFin};

		int count = jdbcTemplate.queryForObject(sql, Integer.class, params);
		return count == 0;
	}
	@GetMapping("/api/enseignants")
	@ResponseBody
	public ResponseEntity<?> getAllEnseignants() {
		try {
			String sql = "SELECT numero_ens, CONCAT(nom_ens, ' ', prenom_ens) as nom_complet FROM enseignants where nom_ens!='Admin' ORDER BY nom_ens";
			List<Map<String, Object>> enseignants = jdbcTemplate.queryForList(sql);
			return ResponseEntity.ok(enseignants);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Erreur lors de la récupération des enseignants"));
		}
	}

	@GetMapping("/api/salles")
	@ResponseBody
	public ResponseEntity<?> getAllSalles() {
		try {
			String sql = "SELECT numero_salle, nom_salle FROM salles ORDER BY nom_salle";
			List<Map<String, Object>> salles = jdbcTemplate.queryForList(sql);
			return ResponseEntity.ok(salles);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Erreur lors de la récupération des salles"));
		}
	}

	@GetMapping("/api/matieres")
	@ResponseBody
	public ResponseEntity<?> getAllMatieres() {
		try {
			String sql = "SELECT code_matiere, nom_matiere FROM matieres ORDER BY nom_matiere";
			List<Map<String, Object>> matieres = jdbcTemplate.queryForList(sql);
			return ResponseEntity.ok(matieres);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Erreur lors de la récupération des matières"));
		}
	}



	private boolean checkEnseignantDisponible(Integer numeroEns, String date, String heureDebut, String heureFin, Integer seanceId) {
		// Si on modifie une séance existante, vérifions d'abord si l'enseignant est le même
		if (seanceId != null) {
			Integer currentEnseignant = jdbcTemplate.queryForObject(
					"SELECT numero_ens FROM seances WHERE id_seance = ?",
					Integer.class,
					seanceId
			);

			// Si c'est le même enseignant et le même jour, pas besoin de vérifier les disponibilités
			if (currentEnseignant != null && currentEnseignant.equals(numeroEns)) {
				String currentDate = jdbcTemplate.queryForObject(
						"SELECT date::text FROM seances WHERE id_seance = ?",
						String.class,
						seanceId
				);
				if (currentDate != null && currentDate.equals(date)) {
					return true;
				}
			}
		}

		// 1. Vérifier le jour de la semaine
		String jourSemaine = jdbcTemplate.queryForObject(
				"SELECT CASE EXTRACT(DOW FROM ?::date)\n" +
						"    WHEN 1 THEN 'LUNDI'\n" +
						"    WHEN 2 THEN 'MARDI'\n" +
						"    WHEN 3 THEN 'MERCREDI'\n" +
						"    WHEN 4 THEN 'JEUDI'\n" +
						"    WHEN 5 THEN 'VENDREDI'\n" +
						"END",
				String.class,
				date
		);

		// 2. Vérifier les disponibilités
		String disponibiliteQuery = "SELECT COUNT(*) FROM disponibilites WHERE numero_ens = ? AND jour = ?";
		int disponible = jdbcTemplate.queryForObject(disponibiliteQuery, Integer.class, numeroEns, jourSemaine);
		System.out.println("Enseignant " + numeroEns + " disponibilités pour " + jourSemaine + ": " + disponible);

		if (disponible == 0) {
			return false;
		}

		// 3. Vérifier les conflits de séances
		String seanceQuery = """
        SELECT COUNT(*) FROM seances 
        WHERE numero_ens = ? 
        AND date = ?::date 
        AND (
            (heure_debut <= ?::time AND heure_fin > ?::time) 
            OR (heure_debut < ?::time AND heure_fin >= ?::time) 
            OR (heure_debut >= ?::time AND heure_fin <= ?::time)
        )
    """;

		if (seanceId != null) {
			seanceQuery += " AND id_seance != ?";
		}

		Object[] params = seanceId != null ?
				new Object[]{numeroEns, date, heureFin, heureDebut, heureFin, heureDebut, heureDebut, heureFin, seanceId} :
				new Object[]{numeroEns, date, heureFin, heureDebut, heureFin, heureDebut, heureDebut, heureFin};

		int count = jdbcTemplate.queryForObject(seanceQuery, Integer.class, params);
		return count == 0;
	}

	@GetMapping("/api/enseignants/me")
	@ResponseBody
	public ResponseEntity<?> getCurrentEnseignant(HttpSession session) {
		try {
			Integer idperso = (Integer) session.getAttribute("userId");
			if (idperso == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(Map.of("error", "Non autorisé"));
			}

			String sql = "SELECT numero_ens, CONCAT(nom_ens, ' ', prenom_ens) as nom_complet " +
					"FROM enseignants WHERE idperso = ?";
			Map<String, Object> enseignant = jdbcTemplate.queryForMap(sql, idperso);

			return ResponseEntity.ok(enseignant);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Erreur lors de la récupération des informations de l'enseignant"));
		}
	}


}
