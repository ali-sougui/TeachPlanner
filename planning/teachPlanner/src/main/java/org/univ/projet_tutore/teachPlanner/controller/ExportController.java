package org.univ.projet_tutore.teachPlanner.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.univ.projet_tutore.teachPlanner.dto.SeanceDetailsDTO;
import org.univ.projet_tutore.teachPlanner.model.Enseignant;
import org.univ.projet_tutore.teachPlanner.model.Personnel;
import org.univ.projet_tutore.teachPlanner.repository.EnseignantRepository;
import org.univ.projet_tutore.teachPlanner.repository.PersonnelRepository;
import org.univ.projet_tutore.teachPlanner.service.SeanceService;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.univ.projet_tutore.teachPlanner.dto.SeanceDetailsDTO;
import org.univ.projet_tutore.teachPlanner.service.SeanceService;

@Controller
public class ExportController {

    @Autowired
    private SeanceService seanceService;

    @Autowired
    private EnseignantRepository enseignantRepository;

    @Autowired
    private PersonnelRepository personnelRepository;

    // Définir les heures de manière configurable
    private static final String[] HEURES = {"08:00", "09:00", "10:00", "11:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00"};
    private static final String[] JOURS = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
    private static final float[] COLUMN_WIDTHS = {100f, 100f, 100f, 100f, 100f, 100f, 100f, 100f};

    @GetMapping("/export")
    public String showExportPage() {
        return "pages/export";  // Le nom du fichier HTML sans l'extension (.html)
    }

    @GetMapping("/export/csv")
    public ResponseEntity<InputStreamResource> exportEmploiDuTempsCSV() {
        try {
            List<SeanceDetailsDTO> seances = seanceService.getAllSeances();
            StringBuilder csvBuilder = new StringBuilder();

            // En-têtes du fichier CSV
            csvBuilder.append("Date,Heure Début,Heure Fin,Enseignant,Salle,Matière\n");

            // Écriture des lignes de données
            seances.forEach(seance -> {
                csvBuilder.append(seance.getDate()).append(",")
                        .append(seance.getHeureDebut()).append(",")
                        .append(seance.getHeureFin()).append(",")
                        .append(seance.getNumeroEns()).append(",")
                        .append(seance.getNumeroSalle()).append(",")
                        .append(seance.getCodeMatiere()).append("\n");
            });

            ByteArrayInputStream in = new ByteArrayInputStream(csvBuilder.toString().getBytes());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=emploi_du_temps.csv");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(new InputStreamResource(in));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<InputStreamResource> exportEmploiDuTempsPDF() {
        try {
            List<SeanceDetailsDTO> seances = seanceService.getAllSeances();
            int nombreDeSemaines = seanceService.getNombreDeSemaines();
            LocalDate premiereDate = seanceService.getPremiereDate();

            if (seances.isEmpty()) {
                return generateEmptyPdfResponse("emploi_du_temps_vide.pdf");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Style et titre général
            addTitle(document, "Emploi du Temps Hebdomadaire");

            int premiereSemaine = premiereDate.get(WeekFields.of(Locale.FRANCE).weekOfYear());

            for (int semaine = premiereSemaine; semaine < premiereSemaine + nombreDeSemaines; semaine++) {
                final int semaineCourante = semaine;

                LocalDate premierJourSemaine = premiereDate.with(WeekFields.of(Locale.FRANCE).weekOfYear(), semaineCourante)
                        .with(WeekFields.of(Locale.FRANCE).dayOfWeek(), 1);

                boolean semaineNonVide = seances.stream().anyMatch(s -> {
                    LocalDate dateSeance = LocalDate.parse(s.getDate());
                    return dateSeance.get(WeekFields.of(Locale.FRANCE).weekOfYear()) == semaineCourante;
                });

                if (!semaineNonVide) continue;

                // Titre de la semaine
                addWeekTitle(document, "Semaine " + semaineCourante);

                // Création du tableau hebdomadaire
                Table table = createWeeklyTable(premierJourSemaine);

                // Structure pour suivre les cellules fusionnées
                Map<LocalDate, Integer> occupiedCells = new HashMap<>();

                for (int h = 0; h < HEURES.length; h++) {
                    String heureDebutSlot = HEURES[h];
                    LocalTime startSlot = LocalTime.parse(heureDebutSlot);
                    LocalTime endSlot = (h + 1 < HEURES.length)
                            ? LocalTime.parse(HEURES[h + 1])
                            : LocalTime.parse("20:00");

                    table.addCell(new Cell().add(new Paragraph(heureDebutSlot + " - " + endSlot))
                            .setTextAlignment(TextAlignment.CENTER));

                    for (int i = 0; i < JOURS.length; i++) {
                        LocalDate dateDuJour = premierJourSemaine.plusDays(i);

                        // Vérifier si la cellule est déjà occupée
                        if (occupiedCells.containsKey(dateDuJour) && occupiedCells.get(dateDuJour) > 0) {
                            occupiedCells.put(dateDuJour, occupiedCells.get(dateDuJour) - 1);
                            continue;
                        }

                        List<SeanceDetailsDTO> seancesActives = seances.stream()
                                .filter(s -> {
                                    LocalDate dateSeance = LocalDate.parse(s.getDate());
                                    LocalTime heureDebutSeance = LocalTime.parse(s.getHeureDebut());
                                    LocalTime heureFinSeance = LocalTime.parse(s.getHeureFin());

                                    return dateSeance.equals(dateDuJour) &&
                                            heureDebutSeance.isBefore(endSlot) &&
                                            heureFinSeance.isAfter(startSlot);
                                })
                                .collect(Collectors.toList());

                        List<SeanceDetailsDTO> sessionsStartingHere = seancesActives.stream()
                                .filter(s -> {
                                    LocalTime heureDebutSeance = LocalTime.parse(s.getHeureDebut());
                                    return !heureDebutSeance.isBefore(startSlot) &&
                                            heureDebutSeance.isBefore(endSlot);
                                })
                                .collect(Collectors.toList());

                        if (sessionsStartingHere.isEmpty()) {
                            table.addCell(new Cell());
                        } else {
                            // Prendre la première séance (simplification)
                            SeanceDetailsDTO seance = sessionsStartingHere.get(0);
                            addSessionToTable(table, seance, occupiedCells, dateDuJour);
                        }
                    }
                }

                // Ajouter le tableau au document
                document.add(table);

                // Ajouter un saut de page après chaque semaine
                if (semaine < premiereSemaine + nombreDeSemaines - 1) {
                    document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                }
            }

            // Fermer le document
            document.close();

            // Créer la réponse HTTP avec le fichier PDF en pièce jointe
            return generatePdfResponse(out, "emploi_du_temps_hebdomadaire.pdf");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/export/pdf/etudiant/{id}")
    public ResponseEntity<InputStreamResource> exportEmploiDuTempsPDFByEtudiant(@PathVariable("id") int numeroEnseignant) {
        System.out.println(numeroEnseignant);

        try {

            Optional<Personnel> perso = personnelRepository.findById(numeroEnseignant);


            // Récupérer l'enseignant à partir du personnel
            Optional <Enseignant> enseignant = enseignantRepository.findByPersonnel(perso);
            if (enseignant == null) {
                return ResponseEntity.notFound().build(); // L'enseignant n'a pas été trouvé
            }

            if (!enseignant.isPresent()) {
                return ResponseEntity.notFound().build(); // L'enseignant n'a pas été trouvé
            }

            // Si l'enseignant est trouvé, récupérez son numéro
            numeroEnseignant = enseignant.get().getNumeroEns();
            System.out.println("Numéro Enseignant: " + numeroEnseignant);
            List<SeanceDetailsDTO> seances = seanceService.getAllSeancesWithDetailsByEnseignant(numeroEnseignant);
            int nombreDeSemaines = seanceService.getNombreDeSemaines();
            LocalDate premiereDate = seanceService.getPremiereDate();

            if (seances.isEmpty() || premiereDate == null) {
                return generateEmptyPdfResponse("emploi_du_temps_vide.pdf");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Style et titre général
            addTitle(document, "Emploi du Temps Hebdomadaire");

            int premiereSemaine = premiereDate.get(WeekFields.of(Locale.FRANCE).weekOfYear());

            for (int semaine = premiereSemaine; semaine < premiereSemaine + nombreDeSemaines; semaine++) {
                final int semaineCourante = semaine;

                LocalDate premierJourSemaine = premiereDate.with(WeekFields.of(Locale.FRANCE).weekOfYear(), semaineCourante)
                        .with(WeekFields.of(Locale.FRANCE).dayOfWeek(), 1);

                boolean semaineNonVide = seances.stream().anyMatch(s -> {
                    LocalDate dateSeance = LocalDate.parse(s.getDate());
                    return dateSeance.get(WeekFields.of(Locale.FRANCE).weekOfYear()) == semaineCourante;
                });

                if (!semaineNonVide) continue;

                // Titre de la semaine
                addWeekTitle(document, "Semaine " + semaineCourante);

                // Création du tableau hebdomadaire
                Table table = createWeeklyTable(premierJourSemaine);

                // Structure pour suivre les cellules fusionnées
                Map<LocalDate, Integer> occupiedCells = new HashMap<>();

                for (int h = 0; h < HEURES.length; h++) {
                    String heureDebutSlot = HEURES[h];
                    LocalTime startSlot = LocalTime.parse(heureDebutSlot);
                    LocalTime endSlot = (h + 1 < HEURES.length)
                            ? LocalTime.parse(HEURES[h + 1])
                            : LocalTime.parse("20:00");

                    table.addCell(new Cell().add(new Paragraph(heureDebutSlot + " - " + endSlot))
                            .setTextAlignment(TextAlignment.CENTER));

                    for (int i = 0; i < JOURS.length; i++) {
                        LocalDate dateDuJour = premierJourSemaine.plusDays(i);

                        // Vérifier si la cellule est déjà occupée
                        if (occupiedCells.containsKey(dateDuJour) && occupiedCells.get(dateDuJour) > 0) {
                            occupiedCells.put(dateDuJour, occupiedCells.get(dateDuJour) - 1);
                            continue;
                        }

                        List<SeanceDetailsDTO> seancesActives = seances.stream()
                                .filter(s -> {
                                    LocalDate dateSeance = LocalDate.parse(s.getDate());
                                    LocalTime heureDebutSeance = LocalTime.parse(s.getHeureDebut());
                                    LocalTime heureFinSeance = LocalTime.parse(s.getHeureFin());

                                    return dateSeance.equals(dateDuJour) &&
                                            heureDebutSeance.isBefore(endSlot) &&
                                            heureFinSeance.isAfter(startSlot);
                                })
                                .collect(Collectors.toList());

                        List<SeanceDetailsDTO> sessionsStartingHere = seancesActives.stream()
                                .filter(s -> {
                                    LocalTime heureDebutSeance = LocalTime.parse(s.getHeureDebut());
                                    return !heureDebutSeance.isBefore(startSlot) &&
                                            heureDebutSeance.isBefore(endSlot);
                                })
                                .collect(Collectors.toList());

                        if (sessionsStartingHere.isEmpty()) {
                            table.addCell(new Cell());
                        } else {
                            // Prendre la première séance (simplification)
                            SeanceDetailsDTO seance = sessionsStartingHere.get(0);
                            addSessionToTable(table, seance, occupiedCells, dateDuJour);
                        }
                    }
                }

                // Ajouter le tableau au document
                document.add(table);

                // Ajouter un saut de page après chaque semaine
                if (semaine < premiereSemaine + nombreDeSemaines - 1) {
                    document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                }
            }

            // Fermer le document
            document.close();

            // Créer la réponse HTTP avec le fichier PDF en pièce jointe
            return generatePdfResponse(out, "emploi_du_temps_hebdomadaire.pdf");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/export/pdf/enseignant/{id}")
    public ResponseEntity<InputStreamResource> exportEmploiDuTempsPDFByEnseignant(@PathVariable("id") int numeroEnseignant) {
        System.out.println(numeroEnseignant);

        try {

            Optional<Personnel> perso = personnelRepository.findById(numeroEnseignant);


            // Récupérer l'enseignant à partir du personnel
            Optional <Enseignant> enseignant = enseignantRepository.findByPersonnel(perso);
            if (enseignant == null) {
                return ResponseEntity.notFound().build(); // L'enseignant n'a pas été trouvé
            }

            if (!enseignant.isPresent()) {
                return ResponseEntity.notFound().build(); // L'enseignant n'a pas été trouvé
            }

            // Si l'enseignant est trouvé, récupérez son numéro
            numeroEnseignant = enseignant.get().getNumeroEns();
            System.out.println("Numéro Enseignant: " + numeroEnseignant);
            List<SeanceDetailsDTO> seances = seanceService.getAllSeancesWithDetailsByEnseignant(numeroEnseignant);
            int nombreDeSemaines = seanceService.getNombreDeSemaines();
            LocalDate premiereDate = seanceService.getPremiereDate();

            if (seances.isEmpty() || premiereDate == null) {
                return generateEmptyPdfResponse("emploi_du_temps_vide.pdf");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Style et titre général
            addTitle(document, "Emploi du Temps Hebdomadaire");

            int premiereSemaine = premiereDate.get(WeekFields.of(Locale.FRANCE).weekOfYear());

            for (int semaine = premiereSemaine; semaine < premiereSemaine + nombreDeSemaines; semaine++) {
                final int semaineCourante = semaine;

                LocalDate premierJourSemaine = premiereDate.with(WeekFields.of(Locale.FRANCE).weekOfYear(), semaineCourante)
                        .with(WeekFields.of(Locale.FRANCE).dayOfWeek(), 1);

                boolean semaineNonVide = seances.stream().anyMatch(s -> {
                    LocalDate dateSeance = LocalDate.parse(s.getDate());
                    return dateSeance.get(WeekFields.of(Locale.FRANCE).weekOfYear()) == semaineCourante;
                });

                if (!semaineNonVide) continue;

                // Titre de la semaine
                addWeekTitle(document, "Semaine " + semaineCourante);

                // Création du tableau hebdomadaire
                Table table = createWeeklyTable(premierJourSemaine);

                // Structure pour suivre les cellules fusionnées
                Map<LocalDate, Integer> occupiedCells = new HashMap<>();

                for (int h = 0; h < HEURES.length; h++) {
                    String heureDebutSlot = HEURES[h];
                    LocalTime startSlot = LocalTime.parse(heureDebutSlot);
                    LocalTime endSlot = (h + 1 < HEURES.length)
                            ? LocalTime.parse(HEURES[h + 1])
                            : LocalTime.parse("20:00");

                    table.addCell(new Cell().add(new Paragraph(heureDebutSlot + " - " + endSlot))
                            .setTextAlignment(TextAlignment.CENTER));

                    for (int i = 0; i < JOURS.length; i++) {
                        LocalDate dateDuJour = premierJourSemaine.plusDays(i);

                        // Vérifier si la cellule est déjà occupée
                        if (occupiedCells.containsKey(dateDuJour) && occupiedCells.get(dateDuJour) > 0) {
                            occupiedCells.put(dateDuJour, occupiedCells.get(dateDuJour) - 1);
                            continue;
                        }

                        List<SeanceDetailsDTO> seancesActives = seances.stream()
                                .filter(s -> {
                                    LocalDate dateSeance = LocalDate.parse(s.getDate());
                                    LocalTime heureDebutSeance = LocalTime.parse(s.getHeureDebut());
                                    LocalTime heureFinSeance = LocalTime.parse(s.getHeureFin());

                                    return dateSeance.equals(dateDuJour) &&
                                            heureDebutSeance.isBefore(endSlot) &&
                                            heureFinSeance.isAfter(startSlot);
                                })
                                .collect(Collectors.toList());

                        List<SeanceDetailsDTO> sessionsStartingHere = seancesActives.stream()
                                .filter(s -> {
                                    LocalTime heureDebutSeance = LocalTime.parse(s.getHeureDebut());
                                    return !heureDebutSeance.isBefore(startSlot) &&
                                            heureDebutSeance.isBefore(endSlot);
                                })
                                .collect(Collectors.toList());

                        if (sessionsStartingHere.isEmpty()) {
                            table.addCell(new Cell());
                        } else {
                            // Prendre la première séance (simplification)
                            SeanceDetailsDTO seance = sessionsStartingHere.get(0);
                            addSessionToTable(table, seance, occupiedCells, dateDuJour);
                        }
                    }
                }

                // Ajouter le tableau au document
                document.add(table);

                // Ajouter un saut de page après chaque semaine
                if (semaine < premiereSemaine + nombreDeSemaines - 1) {
                    document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
                }
            }

            // Fermer le document
            document.close();

            // Créer la réponse HTTP avec le fichier PDF en pièce jointe
            return generatePdfResponse(out, "emploi_du_temps_hebdomadaire.pdf");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/export/print")
    public void printDetails() {
        List<SeanceDetailsDTO> seances = seanceService.getAllSeances();

        seances.forEach(s -> {
            System.out.printf(s.getNomEnseignant() + " - " + s.getNomSalle() + "\n");
        });
    }

    @GetMapping("/export/nbreSemaines")
    public ResponseEntity<Integer> getNombreDeSemaines() {
        int nombreSemaines = seanceService.getNombreDeSemaines();
        return ResponseEntity.ok(nombreSemaines);
    }

    // Méthodes utilitaires

    private ResponseEntity<InputStreamResource> generateEmptyPdfResponse(String filename) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        document.add(new Paragraph("Aucune séance disponible pour l'emploi du temps.")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(14));

        document.close();

        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + filename);

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(in));
    }

    private void addTitle(Document document, String titleText) {
        Paragraph title = new Paragraph(titleText)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(20);
        document.add(title);
        document.add(new Paragraph("\n"));
    }

    private void addWeekTitle(Document document, String weekTitle) {
        Paragraph semaineTitle = new Paragraph(weekTitle)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFontSize(16);
        document.add(semaineTitle);
    }

    private Table createWeeklyTable(LocalDate premierJourSemaine) {
        Table table = new Table(COLUMN_WIDTHS);
        table.addHeaderCell(new Cell().add(new Paragraph("Heures")).setBold().setTextAlignment(TextAlignment.CENTER));
        for (int i = 0; i < JOURS.length; i++) {
            LocalDate dateDuJour = premierJourSemaine.plusDays(i);
            String dateAffichee = dateDuJour.format(DateTimeFormatter.ofPattern("dd/MM"));
            table.addHeaderCell(new Cell().add(new Paragraph(JOURS[i] + " (" + dateAffichee + ")"))
                    .setBold().setTextAlignment(TextAlignment.CENTER));
        }
        return table;
    }

    private void addSessionToTable(Table table, SeanceDetailsDTO seance, Map<LocalDate, Integer> occupiedCells, LocalDate dateDuJour) {
        LocalTime heureDebutSeance = LocalTime.parse(seance.getHeureDebut());
        LocalTime heureFinSeance = LocalTime.parse(seance.getHeureFin());

        long duration = ChronoUnit.HOURS.between(heureDebutSeance, heureFinSeance);
        int rowSpan = (int) duration;

        StringBuilder contenu = new StringBuilder();
        contenu.append(seance.getNomMatiere())
                .append("\n")
                .append(seance.getNomEnseignant())
                .append("\n")
                .append(seance.getNomSalle())
                .append("\n\n");

        Cell cell = new Cell(rowSpan, 1)
                .add(new Paragraph(contenu.toString()))
                .setTextAlignment(TextAlignment.CENTER);
        table.addCell(cell);

        // Marquer les cellules comme occupées
        occupiedCells.put(dateDuJour, rowSpan - 1);
    }

    private ResponseEntity<InputStreamResource> generatePdfResponse(ByteArrayOutputStream out, String filename) throws Exception {
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + filename);

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(in));
    }
}