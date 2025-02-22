package org.univ.projet_tutore.teachPlanner.controller;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.List;


import com.itextpdf.layout.properties.TextAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.univ.projet_tutore.teachPlanner.dto.SeanceDetailsDTO;
import org.univ.projet_tutore.teachPlanner.service.SeanceService;


import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.util.Optional;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;



@Controller
public class ExportController {

    @Autowired
    private SeanceService  seanceService;

    @GetMapping("/export")
    public String showExportPage() {
        return "pages/export";  // Le nom du fichier HTML sans l'extension (.html)
    }



    @GetMapping("/export/csv")
    public ResponseEntity<InputStreamResource> exportEmploiDuTempsCSV() {
        try {
            List<SeanceDetailsDTO> seances = seanceService.getAllSeances();
            StringBuilder csvBuilder = new StringBuilder();



            // En-têtes du fichier CSV
            csvBuilder.append("Date,Heure Début,Heure Fin,Enseignant,Salle,Matière\n");

            // Écriture des lignes de données
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
            // Récupération des données à exporter
            List<SeanceDetailsDTO> seances = seanceService.getAllSeances();
            int nombreDeSemaines = seanceService.getNombreDeSemaines();
            System.out.println("le nbre : "+nombreDeSemaines);

            // Création du flux de sortie pour le PDF
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            // Initialisation du document PDF
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Titre du document
            Paragraph title = new Paragraph("Emploi du Temps Hebdomadaire")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold()
                    .setFontSize(20);
            document.add(title);

            // Ajout d'un espace après le titre
            document.add(new Paragraph("\n"));

            // Définition des jours de la semaine
            String[] jours = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
            float[] columnWidths = {100f, 100f, 100f, 100f, 100f, 100f, 100f, 100f}; // 1 colonne pour heures + 7 pour jours

            // Créneaux horaires
            String[] heures = {"08:00 - 09:00", "09:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00", "14:00 - 15:00", "15:00 - 16:00", "16:00 - 17:00"};

            // Génération des tableaux pour chaque semaine
            for (int semaine = 1; semaine <= nombreDeSemaines; semaine++) {
                // Titre de la semaine
                Paragraph semaineTitle = new Paragraph("Semaine " + semaine)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setBold()
                        .setFontSize(16);
                document.add(semaineTitle);

                // Calcul du premier jour de la semaine (Lundi)
                LocalDate premierJourSemaine = LocalDate.now().with(WeekFields.of(Locale.FRANCE).weekOfYear(), semaine)
                        .with(WeekFields.of(Locale.FRANCE).dayOfWeek(), 1);

                // Création du tableau hebdomadaire
                Table table = new Table(columnWidths);

                // Ligne d'en-tête : Heures + Jours de la semaine avec dates
                table.addHeaderCell(new Cell().add(new Paragraph("Heures")).setBold().setTextAlignment(TextAlignment.CENTER));
                for (int i = 0; i < jours.length; i++) {
                    LocalDate dateDuJour = premierJourSemaine.plusDays(i);
                    String dateAffichee = dateDuJour.format(DateTimeFormatter.ofPattern("dd/MM"));
                    table.addHeaderCell(new Cell().add(new Paragraph(jours[i] + " (" + dateAffichee + ")"))
                            .setBold().setTextAlignment(TextAlignment.CENTER));
                }

                // Parcours des créneaux horaires
                for (String heure : heures) {
                    table.addCell(new Cell().add(new Paragraph(heure)).setTextAlignment(TextAlignment.CENTER));

                    // Parcours des jours de la semaine
                    for (int i = 0; i < jours.length; i++) {
                        LocalDate dateDuJour = premierJourSemaine.plusDays(i);

                        // Filtrer les séances pour cette semaine, ce jour et cette heure
                        Optional<SeanceDetailsDTO> seanceOption = seances.stream()
                                .filter(s -> {
                                    LocalDate dateSeance = LocalDate.parse(s.getDate());
                                    return dateSeance.equals(dateDuJour) && s.getHeureDebut().equals(heure.split(" - ")[0]);
                                })
                                .findFirst();

                        // Ajouter la séance dans la cellule ou laisser vide
                        if (seanceOption.isPresent()) {
                            SeanceDetailsDTO seance = seanceOption.get();
                            String contenu = seance.getNomMatiere() + "\n" + seance.getNomEnseignant() + "\n" + seance.getNomSalle();
                            table.addCell(new Cell().add(new Paragraph(contenu)).setTextAlignment(TextAlignment.CENTER));
                        } else {
                            table.addCell(new Cell());
                        }
                    }
                }

                // Ajout du tableau au document
                document.add(table);

                // Ajout d'un espace après chaque tableau
                document.add(new Paragraph("\n"));
            }

            // Fermeture du document
            document.close();

            // Création de la réponse HTTP avec le fichier PDF en pièce jointe
            ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=emploi_du_temps_hebdomadaire.pdf");

            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new InputStreamResource(in));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }




    @GetMapping("/export/print")
    public void printDetails() {
        List<SeanceDetailsDTO> seances = seanceService.getAllSeances();

        seances.forEach(s -> {
            System.out.printf (s.getNomEnseignant() , s.getNomSalle() + "\n");
        });
    }


    @GetMapping("/export/nbreSemaines")
    public ResponseEntity<Integer> getNombreDeSemaines() {
        int nombreSemaines = seanceService.getNombreDeSemaines();
        return ResponseEntity.ok(nombreSemaines);
    }

}
