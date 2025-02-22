document.addEventListener('DOMContentLoaded', function() {
    const calendarEl = document.getElementById('calendar');
    const modal = new bootstrap.Modal(document.getElementById('seanceModal'));
    let currentSeance = null;
    let calendar = null;
    let allSeances = [];
    // Définition des couleurs pour les matières
    const couleursMatieres = {
        "Mathematique": "#ff5733",
        "Physique": "#33ff57",
        "Informatique": "#3357ff",
        "Chimie": "#ff33a6",
        "Anglais": "#a633ff",
        "Histoire": "#ffbb33",
        "Géographie": "#33ffbb"
    };

    // Initialiser le calendrier
    calendar = new FullCalendar.Calendar(calendarEl, {
        initialView: 'timeGridWeek',
        headerToolbar: {
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay'
        },
        locale: 'fr',
        firstDay: 1,
        weekNumbers: true,
        weekText: 'S',
        slotMinTime: '08:00:00',
        slotMaxTime: '20:00:00',
        allDaySlot: false,
        height: 'auto',
        expandRows: true,
        stickyHeaderDates: true,
        nowIndicator: true,
        editable: true,
        selectable: true,
        selectMirror: true,
        businessHours: {
            daysOfWeek: [1, 2, 3, 4, 5], // Lundi à Vendredi
            startTime: '08:00',
            endTime: '20:00',
        },
        selectConstraint: {
            daysOfWeek: [1, 2, 3, 4, 5] // Empêcher la sélection le weekend
        },
        dateClick: function(info) {
            const clickedDate = new Date(info.date); // Convertir en objet Date JS
            const dayOfWeek = clickedDate.getDay();

            // Empêcher la création de séances le weekend (0 = Dimanche, 6 = Samedi)
            if (dayOfWeek === 0 || dayOfWeek === 6) {
                alert('Les séances ne peuvent pas être créées le weekend');
                return;
            }

            currentSeance = null;
            document.getElementById('seanceForm').reset();

            // Extraction correcte de la date et de l'heure
            document.getElementById('date').value = clickedDate.toISOString().split('T')[0]; // Format YYYY-MM-DD
            document.getElementById('heureDebut').value = clickedDate.toTimeString().slice(0, 5); // HH:MM

            // Calculer l'heure de fin (1 heure après l'heure de début)
            const heureFin = new Date(clickedDate.getTime() + 60 * 60 * 1000);
            document.getElementById('heureFin').value = heureFin.toTimeString().slice(0, 5);

            document.getElementById('modalTitle').textContent = 'Ajouter une Séance';
            document.getElementById('remove').style.display = 'none';
            modal.show();
        }
        ,
        eventClick: function(info) {
            currentSeance = info.event;
            document.getElementById('modalTitle').textContent = 'Modifier la Séance';

            const date = currentSeance.start.toISOString().split('T')[0];
            const heureDebut = currentSeance.start.toTimeString().slice(0, 5);
            const heureFin = currentSeance.end.toTimeString().slice(0, 5);
            console.log("la date dans modifie : ",info.event.start.toISOString().split('T')[0]);

            document.getElementById('date').value = date;
            document.getElementById('heureDebut').value = heureDebut;
            document.getElementById('heureFin').value = heureFin;
            document.getElementById('numeroEns').value = currentSeance.extendedProps.numeroEns;
            document.getElementById('numeroSalle').value = currentSeance.extendedProps.numeroSalle;
            document.getElementById('codeMatiere').value = currentSeance.extendedProps.codeMatiere;

            document.getElementById('remove').style.display = 'block';

            modal.show();
        },
        eventDrop: function(info) {
            const droppedDate = info.event.start;
            const dayOfWeek = droppedDate.getDay();

            // Empêcher le déplacement vers le weekend
            if (dayOfWeek === 0 || dayOfWeek === 6) {
                alert('Les séances ne peuvent pas être déplacées au weekend');
                info.revert();
                return;
            }


            const seance = {
                date: info.event.start.toISOString().split('T')[0],
                heureDebut: info.event.start.toTimeString().slice(0, 5),
                heureFin: info.event.end.toTimeString().slice(0, 5),
                numeroEns: info.event.extendedProps.numeroEns,
                numeroSalle: info.event.extendedProps.numeroSalle,
                codeMatiere: info.event.extendedProps.codeMatiere
            };

            updateSeance(info.event.id, seance)
                .catch(error => {
                    console.error('Erreur lors de la mise à jour:', error);
                    info.revert();
                });
        }
    });

    calendar.render();
    // Ajouter après l'initialisation du calendrier
    function setupEventListeners() {
        // Gestionnaire de suppression
        document.getElementById('remove').addEventListener('click', function(e) {
            e.preventDefault();
            e.stopPropagation();
            console.log("Bouton supprimer cliqué");

            if (!currentSeance || !currentSeance.id) {
                console.log("Pas de séance sélectionnée");
                return;
            }

            if (confirm('Êtes-vous sûr de vouloir supprimer cette séance ?')) {
                console.log("Suppression de la séance", currentSeance.id);
                deleteSeance(currentSeance.id)
                    .then(() => {
                        console.log("Séance supprimée avec succès");
                        modal.hide();
                        loadSeances();
                    })
                    .catch(error => {
                        console.error('Erreur lors de la suppression:', error);
                        alert('Erreur lors de la suppression de la séance');
                    });
            }
        });

        // Gestionnaire d'export PDF amélioré
        function exportToPDF() {
            // Sauvegarder la vue actuelle
            const currentView = calendar.view.type;

            // Passer en vue semaine pour l'export
            calendar.changeView('timeGridWeek');

            // Créer le conteneur d'export
            const exportContainer = document.createElement('div');
            exportContainer.className = 'export-container';

            // Ajouter l'en-tête
            const header = document.createElement('div');
            header.className = 'export-header';
            header.innerHTML = `
            <h2>Emploi du Temps</h2>
            <p>${new Date().toLocaleDateString('fr-FR', {
                weekday: 'long',
                year: 'numeric',
                month: 'long',
                day: 'numeric'
            })}</p>
        `;
            exportContainer.appendChild(header);

            // Cloner le calendrier
            const calendarClone = calendar.el.cloneNode(true);
            calendarClone.className = 'export-calendar';
            exportContainer.appendChild(calendarClone);

            // Ajouter temporairement à la page
            document.body.appendChild(exportContainer);

            // Configuration de l'export
            const opt = {
                filename: 'emploi-du-temps.pdf',
                image: { type: 'jpeg', quality: 1 },
                html2canvas: {
                    scale: 2,
                    useCORS: true,
                    letterRendering: true,
                    width: exportContainer.offsetWidth,
                    height: exportContainer.offsetHeight + 50, // Marge supplémentaire
                    onclone: function(clonedDoc) {
                        const clonedContainer = clonedDoc.querySelector('.export-container');
                        clonedContainer.style.width = '100%';
                        clonedContainer.style.maxWidth = '1200px';
                        clonedContainer.style.margin = '0 auto';
                        clonedContainer.style.padding = '20px';

                        // Ajuster les styles du calendrier cloné
                        const calendarElement = clonedContainer.querySelector('.fc');
                        if (calendarElement) {
                            calendarElement.style.height = 'auto';
                            calendarElement.style.minHeight = '500px';
                        }
                    }
                },
                jsPDF: {
                    unit: 'px',
                    format: [1200, 842], // Format personnalisé pour éviter les coupures
                    orientation: 'landscape',
                    compress: true
                }
            };

            // Générer le PDF
            html2pdf()
                .set(opt)
                .from(exportContainer)
                .save()
                .then(() => {
                    // Nettoyer et restaurer la vue
                    document.body.removeChild(exportContainer);
                    calendar.changeView(currentView);
                })
                .catch(err => {
                    console.error('Erreur lors de la génération du PDF:', err);
                    alert('Erreur lors de la génération du PDF');
                    document.body.removeChild(exportContainer);
                    calendar.changeView(currentView);
                });
        }

        // Attacher la fonction au bouton d'export
        document.getElementById('exportPDF').addEventListener('click', exportToPDF);
    }
    setupEventListeners();

    // Charger les séances
    loadSeances();

    // Gestionnaires des filtres
    document.getElementById('filterEnseignant').addEventListener('change', applyFilters);
    document.getElementById('filterSalle').addEventListener('change', applyFilters);
    document.getElementById('filterMatiere').addEventListener('change', applyFilters);

    function loadSeances() {
        fetch('/api/seances')
            .then(response => response.json())
            .then(seances => {
                allSeances = seances; // Stocker toutes les séances
                updateFiltersOptions(seances);
                displaySeances(seances);
            })
            .catch(error => {
                console.error('Erreur lors du chargement des séances:', error);
            });
    }

    function updateFiltersOptions(seances) {
        const enseignants = new Set();
        const salles = new Set();
        const matieres = new Set();

        seances.forEach(seance => {
            if (seance.nomEnseignant) enseignants.add(seance.nomEnseignant);
            if (seance.nomSalle) salles.add(seance.nomSalle);
            if (seance.nomMatiere) matieres.add(seance.nomMatiere);
        });

        // Mettre à jour les options des filtres
        updateSelectOptions('filterEnseignant', enseignants);
        updateSelectOptions('filterSalle', salles);
        updateSelectOptions('filterMatiere', matieres);
    }

    function updateSelectOptions(selectId, options) {
        const select = document.getElementById(selectId);
        const currentValue = select.value;

        // Garder l'option "Tous"
        select.innerHTML = '<option value="">Tous</option>';

        // Ajouter les nouvelles options
        Array.from(options).sort().forEach(option => {
            const optionElement = document.createElement('option');
            optionElement.value = option;
            optionElement.textContent = option;
            select.appendChild(optionElement);
        });

        // Restaurer la valeur sélectionnée si elle existe toujours
        if (Array.from(options).includes(currentValue)) {
            select.value = currentValue;
        }
    }

    function applyFilters() {
        const enseignantFilter = document.getElementById('filterEnseignant').value;
        const salleFilter = document.getElementById('filterSalle').value;
        const matiereFilter = document.getElementById('filterMatiere').value;

        const filteredSeances = allSeances.filter(seance => {
            return (!enseignantFilter || seance.nomEnseignant === enseignantFilter) &&
                (!salleFilter || seance.nomSalle === salleFilter) &&
                (!matiereFilter || seance.nomMatiere === matiereFilter);
        });

        displaySeances(filteredSeances);
    }

    function displaySeances(seances) {
        calendar.removeAllEvents();
        seances.forEach(seance => {
            calendar.addEvent({
                id: seance.idSeance,
                title: `${seance.nomMatiere}\n${seance.nomEnseignant}\nSalle: ${seance.nomSalle}`,
                start: `${seance.date}T${seance.heureDebut}`,
                end: `${seance.date}T${seance.heureFin}`,
                backgroundColor: getEventColor(seance.nomMatiere),
                extendedProps: {
                    numeroEns: seance.numeroEns,
                    numeroSalle: seance.numeroSalle,
                    codeMatiere: seance.codeMatiere,
                    nomEnseignant: seance.nomEnseignant,
                    nomMatiere: seance.nomMatiere,
                    nomSalle: seance.nomSalle
                }
            });
        });
    }


    function getEventColor(matiere) {
        return couleursMatieres[matiere] || "#808080";
    }

    // Gestionnaire pour le bouton de sauvegarde
    document.getElementById('saveSeance').addEventListener('click', function() {
        const formData = new FormData(document.getElementById('seanceForm'));
        const date = formData.get('date');
        const dayOfWeek = new Date(date).getDay();

        // Vérifier si c'est un weekend
        if (dayOfWeek === 0 || dayOfWeek === 6) {
            alert('Les séances ne peuvent pas être créées le weekend');
            return;
        }

        const seance = {
            date: formData.get('date'),
            heureDebut: formData.get('heureDebut'),
            heureFin: formData.get('heureFin'),
            numeroEns: parseInt(formData.get('numeroEns')),
            numeroSalle: parseInt(formData.get('numeroSalle')),
            codeMatiere: parseInt(formData.get('codeMatiere'))
        };

        if(formData.get('heureDebut')>=formData.get('heureFin')){
            alert("erreur l'heure debut ne peut pas etre > a l'heure de fin");
            return;
        }
        if (currentSeance) {
            updateSeance(currentSeance.id, seance)
                .then(() => {
                    modal.hide();
                    loadSeances();
                })
                .catch(error => {
                    console.error('Erreur lors de la mise à jour:', error);
                    alert('Erreur lors de la mise à jour de la séance');
                });
        } else {
            createSeance(seance)
                .then(() => {
                    modal.hide();
                    loadSeances();
                })
                .catch(error => {
                    console.error('Erreur lors de la création:', error);
                    alert('Erreur lors de la création de la séance');
                });
        }
    });

    function createSeance(seance) {
        return fetch('/api/seances', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(seance)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Erreur lors de la création de la séance');
                }
                return response.json();
            });
    }

    function updateSeance(id, seance) {
        return fetch(`/api/seances/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(seance)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Erreur lors de la mise à jour de la séance');
                }
                return response.json();
            });
    }
    function deleteSeance(id) {
        return fetch(`/api/seances/${id}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Erreur lors de la suppression de la séance');
                }
            });
    }
});

// Fonction pour préparer l'ajout d'une nouvelle séance
function prepareNewSeance() {
    document.getElementById('seanceForm').reset();
    document.getElementById('modalTitle').textContent = 'Ajouter une Séance';
    document.getElementById('date').valueAsDate = new Date();
    document.getElementById('remove').style.display = 'none';
}