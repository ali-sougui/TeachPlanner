document.addEventListener("DOMContentLoaded", function () {
    let userId = sessionStorage.getItem("enseignantId") || "6";
    sessionStorage.setItem("enseignantId", userId);

    fetch(`/api/emplois/enseignant/${userId}`)
        .then(response => response.json())
        .then(seances => {
            document.getElementById("nomProf").textContent = sessionStorage.getItem("enseignantNom") || "Professeur";

            let calendarEl = document.getElementById('calendar');
            let calendar = new FullCalendar.Calendar(calendarEl, {
                initialView: "timeGridWeek", // Vue par défaut : Semaine
                locale: "fr", // Définir la locale en français
                firstDay: 1, // Lundi comme premier jour
                weekNumbers: true,
                weekText: "Semaine",
                slotMinTime: "08:00:00",
                slotMaxTime: "20:00:00",
                allDaySlot: false,
                height: "auto",
                nowIndicator: true,
                headerToolbar: {
                    left: "prev,next today",
                    center: "title",
                    right: "dayGridMonth,timeGridWeek,timeGridDay print" // Boutons pour les vues Mois, Semaine, Jour et Impression
                },
                buttonText: {
                    today: "Aujourd'hui",
                    month: "Mois",
                    week: "Semaine",
                    day: "Jour",
                    list: "Agenda",
                    print: "Imprimer"
                },
                slotLabelFormat: {
                    hour: "2-digit",
                    minute: "2-digit",
                    omitZeroMinute: false,
                    meridiem: false // Format 24h sans AM/PM
                },
                businessHours: {
                    daysOfWeek: [1, 2, 3, 4, 5], // Lundi à Vendredi
                    startTime: "08:00",
                    endTime: "20:00"
                },
                selectable: false, // Désactiver la sélection
                editable: false, // Désactiver les modifications
                eventClick: function () {
                    alert("Vous ne pouvez pas modifier cet emploi du temps.");
                },
                events: seances.map(seance => ({
                    title: `${seance.nomMatiere} (Salle: ${seance.nomSalle})`,
                    start: `${seance.date}T${seance.heureDebut}`,
                    end: `${seance.date}T${seance.heureFin}`
                }))
            });

            // Ajouter un bouton d'impression
            calendarEl.insertAdjacentHTML("beforebegin", '<button id="printCalendar" class="btn btn-primary mb-3">🖨️ Imprimer le planning</button>');
            document.getElementById("printCalendar").addEventListener("click", function () {
                window.print(); // Fonction d'impression du navigateur
            });

            calendar.render();
        })
        .catch(error => console.error("Erreur lors du chargement des séances :", error));
});