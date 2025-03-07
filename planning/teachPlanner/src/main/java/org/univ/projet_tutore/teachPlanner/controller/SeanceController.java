package org.univ.projet_tutore.teachPlanner.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.univ.projet_tutore.teachPlanner.model.Seance;
import org.univ.projet_tutore.teachPlanner.service.EnseignantService;
import org.univ.projet_tutore.teachPlanner.service.EtudiantService;
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
	@Autowired
	private EnseignantService enseignantService;

	@Autowired
	private EtudiantService etudiantService;

	@GetMapping("/seances")
	public String showCalendar(HttpSession session) {
		// Vérifier si l'utilisateur est connecté
		if (session.getAttribute("userLoggedIn") == null) {
			return "redirect:/login";
		}

		String role = (String) session.getAttribute("role");
		if (role == null) {
			return "redirect:/login";
		}

		switch (role) {
			case "ADMIN":
				return "pages/seance";
			case "ENSEIGNANT":
				return "pages/seance";
			case "ETUDIANT":
				return "pages/etudiant-edt";
			default:
				return "redirect:/login";
		}
	}

	@GetMapping("/api/seances")
	public ResponseEntity<Map<String, Object>> getAllSeances(HttpSession session) {
		if (session.getAttribute("userLoggedIn") == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(Map.of("error", "Non autorisé"));
		}

		try {
			String role = (String) session.getAttribute("role");
			Integer userId = (Integer) session.getAttribute("userId");
			List<SeanceDetailsDTO> seances;
			String nomComplet;

			switch (role) {
				case "ADMIN":
					seances = seanceService.getAllSeances();
					nomComplet = "Administrateur";
					break;
				case "ENSEIGNANT":
					// Récupérer le numéro d'enseignant à partir de l'ID personnel
					var enseignant = enseignantService.findByPersonnel(userId);
					seances = seanceService.getSeancesByEnseignant(enseignant.getNumeroEns());
					nomComplet = enseignant.getNomEns() + " " + enseignant.getPrenomEns();
					break;
				case "ETUDIANT":
					// Récupérer le numéro d'étudiant à partir de l'ID personnel
					var etudiant = etudiantService.findByPersonnel(userId);
					seances = seanceService.getSeancesByEtudiant(etudiant.getNumeroEtudiant());
					nomComplet = etudiant.getNom() + " " + etudiant.getPrenom();
					break;
				default:
					return ResponseEntity.status(HttpStatus.FORBIDDEN)
							.body(Map.of("error", "Rôle non autorisé"));
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
	public ResponseEntity<?> createSeance(@RequestBody SeanceDTO seanceDTO, HttpSession session) {
		try {
			// Vérifier si l'utilisateur est connecté
			if (session.getAttribute("userLoggedIn") == null) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(Map.of("error", "Non autorisé"));
			}

			// Vérifier si l'utilisateur est admin
			if (!Boolean.TRUE.equals(session.getAttribute("isAdmin"))) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body(Map.of("error", "Action non autorisée"));
			}

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

			Seance seance = seanceService.createSeance(seanceDTO);
			return ResponseEntity.ok(seanceService.convertToSeanceDetailsDTO(seance));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", e.getMessage()));
		}
	}


	@PutMapping("/api/seances/{id}")
	@ResponseBody
	public ResponseEntity<Object> updateSeance(@PathVariable Integer id, @RequestBody SeanceDTO seanceDTO, HttpSession session) {
		if (!isAdmin(session)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(Map.of("error", "Action non autorisée"));
		}

		try {
			Seance seance = seanceService.updateSeance(id, seanceDTO);
			return ResponseEntity.ok(seanceService.convertToSeanceDetailsDTO(seance));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", e.getMessage()));
		}
	}

	@DeleteMapping("/api/seances/{id}")
	@ResponseBody
	public ResponseEntity<Object> deleteSeance(@PathVariable Integer id, HttpSession session) {
		if (!isAdmin(session)) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(Map.of("error", "Action non autorisée"));
		}

		try {
			seanceService.deleteSeance(id);
			return ResponseEntity.ok(Map.of("message", "Séance supprimée avec succès"));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", e.getMessage()));
		}
	}



	@GetMapping("/api/seances/etudiant/{numeroEtudiant}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getSeancesEtudiant(@PathVariable Integer numeroEtudiant, HttpSession session) {
		try {
			// Vérifier que l'étudiant accède à ses propres séances
			Integer sessionUserId = (Integer) session.getAttribute("userId");
			String role = (String) session.getAttribute("role");

			if (!role.equals("ETUDIANT")) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body(Map.of("error", "Action non autorisée"));
			}

			// Récupérer l'étudiant à partir de l'ID personnel
			var etudiant = etudiantService.findByPersonnel(sessionUserId);
			List<SeanceDetailsDTO> seances = seanceService.getSeancesByEtudiant(etudiant.getNumeroEtudiant());

			Map<String, Object> response = new HashMap<>();
			response.put("seances", seances);
			response.put("nomComplet", etudiant.getNom() + " " + etudiant.getPrenom());

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Erreur lors de la récupération des séances: " + e.getMessage()));
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

			String role = (String) session.getAttribute("role");
			Integer userId = (Integer) session.getAttribute("userId");
			Integer enseignantId = null;

			if (role.equals("ENSEIGNANT")) {
				enseignantId = jdbcTemplate.queryForObject(
						"SELECT numero_ens FROM enseignants WHERE idperso = ?",
						Integer.class,
						userId
				);
			}

			Map<String, Long> stats = seanceService.getDashboardStats(enseignantId);
			return ResponseEntity.ok(stats);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(Map.of("error", "Erreur lors de la récupération des statistiques: " + e.getMessage()));
		}
	}

	//	private boolean isAdmin(HttpSession session) {
//		return "ADMIN".equals(session.getAttribute("role"));
//	}
	private boolean isAdmin(HttpSession session) {
		return Boolean.TRUE.equals(session.getAttribute("isAdmin"));
	}

	private boolean checkSalleDisponible(Integer numeroSalle, String date, String heureDebut, String heureFin, Integer seanceId) {
		String sql = """
            SELECT COUNT(*) FROM seances 
            WHERE numero_salle = ? 
            AND date = ?::date 
            AND (
                (heure_debut < ?::time AND heure_fin > ?::time) 
                OR (heure_debut < ?::time AND heure_fin > ?::time)
            )
        """;

		if (seanceId != null) {
			sql += " AND id_seance != ?";
		}

		Object[] params = seanceId != null ?
				new Object[]{numeroSalle, date, heureFin, heureDebut, heureFin, heureDebut, seanceId} :
				new Object[]{numeroSalle, date, heureFin, heureDebut, heureFin, heureDebut};

		int count = jdbcTemplate.queryForObject(sql, Integer.class, params);
		return count == 0;
	}

	private boolean checkEnseignantDisponible(Integer numeroEns, String date, String heureDebut, String heureFin, Integer seanceId) {
		if (seanceId != null) {
			Integer currentEnseignant = jdbcTemplate.queryForObject(
					"SELECT numero_ens FROM seances WHERE id_seance = ?",
					Integer.class,
					seanceId
			);

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

		String disponibiliteQuery = "SELECT COUNT(*) FROM disponibilites WHERE numero_ens = ? AND jour = ?";
		int disponible = jdbcTemplate.queryForObject(disponibiliteQuery, Integer.class, numeroEns, jourSemaine);

		if (disponible == 0) {
			return false;
		}

		String seanceQuery = """
        SELECT COUNT(*) FROM seances 
        WHERE numero_ens = ? 
        AND date = ?::date 
        AND (
            (heure_debut < ?::time AND heure_fin > ?::time) 
            OR (heure_debut < ?::time AND heure_fin > ?::time) 
            OR (heure_debut > ?::time AND heure_fin < ?::time)
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

	@GetMapping("/api/seance/enseignants")
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
	@GetMapping("/password/modify")
	public String showModifyPage() {
		return "pages/modifyPassword";
	}
}