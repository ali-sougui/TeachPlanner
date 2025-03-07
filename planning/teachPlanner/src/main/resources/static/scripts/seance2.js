let notifications = [];

    function updateDashboardStats() {
        fetch('/api/dashboard-stats')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Erreur lors de la récupération des statistiques');
                }
                return response.json();
            })
            .then(stats => {
                console.log('Statistiques reçues:', stats); // Pour le débogage
                Object.entries(stats).forEach(([key, value]) => {
                    const element = document.querySelector(`[data-stat="${key}"]`);
                    if (element) {
                        element.textContent = value;
                        element.classList.add('stats-update-animation');
                        setTimeout(() => element.classList.remove('stats-update-animation'), 500);
                    }
                });
            })
            .catch(error => {
                console.error('Erreur lors de la mise à jour des statistiques:', error);
            });
    }

    function addNotification(message, type = 'info') {
        notifications.push({
            id: Date.now(),
            message,
            type,
            read: false,
            timestamp: new Date()
        });
        updateNotificationBadge();
        updateNotificationList();
    }

    function updateNotificationBadge() {
        const unreadCount = notifications.filter(n => !n.read).length;
        const badge = document.getElementById('notificationBadge');
        badge.textContent = unreadCount;
        badge.style.display = unreadCount > 0 ? 'block' : 'none';
    }

    function updateNotificationList() {
        const list = document.querySelector('.notification-list');
        list.innerHTML = notifications.length > 0 ? notifications.map(n => `
            <div class="notification-item ${n.read ? 'read' : ''}" data-id="${n.id}">
                <div class="d-flex justify-content-between align-items-start">
                    <div>
                        <div class="notification-message">${n.message}</div>
                        <small class="text-muted">${new Date(n.timestamp).toLocaleString()}</small>

                    </div>
                    ${!n.read ? '<span class="badge bg-primary">Nouveau</span>' : ''}
                </div>
            </div>
        `).join('') : '<div class="p-3 text-center text-muted">Aucune notification</div>';
    }


    document.addEventListener('DOMContentLoaded', function() {
        // Récupération des variables de session Thymeleaf
        const isAdmin = /*[[${session.isAdmin}]]*/ false;
        const userId = /*[[${session.userId}]]*/ null;
        const userEmail = /*[[${session.userEmail}]]*/ '';
        const lastConnexion = /*[[${session.lastConnexion}]]*/ '';

        // Fonction pour afficher des alertes
        function showAlert(message, type) {
            const alertContainer = document.getElementById('alertContainer');
            const alert = document.createElement('div');
            alert.className = `alert alert-${type} alert-dismissible fade show`;
            alert.innerHTML = `
                    ${message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                `;
            alertContainer.appendChild(alert);

            // Auto-dismiss après 5 secondes
            setTimeout(() => {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            }, 5000);
        }

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
        document.getElementById("voirdispos").addEventListener("click", function(event) {
            event.preventDefault(); // Empêche le lien de rediriger
            let form = document.getElementById("disponibiliteSection");
            form.style.display = (form.style.display === "none" || form.style.display === "") ? "block" : "none";
        });

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
            editable: isAdmin,
            selectable: isAdmin,
            selectMirror: true,
            businessHours: {
                daysOfWeek: [1, 2, 3, 4, 5],
                startTime: '08:00',
                endTime: '20:00',
            },
            selectConstraint: {
                daysOfWeek: [1, 2, 3, 4, 5]
            },
            dateClick: function(info) {
                if (!isAdmin) return;

                const clickedDate = new Date(info.date);
                const dayOfWeek = clickedDate.getDay();

                if (dayOfWeek === 0 || dayOfWeek === 6) {
                    // alert('Les séances ne peuvent pas être créées le weekend');
                    showAlert('Les séances ne peuvent pas être créées le weekend', 'danger');
                    return;
                }

                currentSeance = null;
                document.getElementById('seanceForm').reset();
                document.getElementById('date').value = info.dateStr.split('T')[0];
                document.getElementById('heureDebut').value = info.dateStr.split('T')[1].slice(0, 5);

                const heureFin = new Date(clickedDate.getTime() + 60 * 60 * 1000);
                document.getElementById('heureFin').value = heureFin.toTimeString().slice(0, 5);

                document.getElementById('modalTitle').textContent = 'Ajouter une Séance';
                document.getElementById('remove').style.display = 'none';

                loadAllSelects().then(() => {
                    modal.show();
                });
            },

            // Dans la fonction eventClick, ajout de la sélection des groupes
            eventClick: function(info) {
                if (!isAdmin) return;

                currentSeance = info.event;
                document.getElementById('modalTitle').textContent = 'Modifier la Séance';
                document.getElementById('date').value = info.event.startStr.split('T')[0];
                document.getElementById('heureDebut').value = info.event.startStr.split('T')[1].slice(0, 5);
                document.getElementById('heureFin').value = info.event.endStr.split('T')[1].slice(0, 5);

                loadAllSelects().then(() => {
                    const form = document.getElementById('seanceForm');

                    // Stockage des valeurs originales
                    form.setAttribute('data-original-numero-ens', info.event.extendedProps.numeroEns);
                    form.setAttribute('data-original-numero-salle', info.event.extendedProps.numeroSalle);
                    form.setAttribute('data-original-code-matiere', info.event.extendedProps.codeMatiere);
                    form.setAttribute('data-original-date', info.event.startStr.split('T')[0]);
                    form.setAttribute('data-original-heure-debut', info.event.startStr.split('T')[1].slice(0, 5));
                    form.setAttribute('data-original-heure-fin', info.event.endStr.split('T')[1].slice(0, 5));

                    // Définir les valeurs dans les combobox
                    window.comboboxEns.setValue(info.event.extendedProps.numeroEns);
                    window.comboboxSalle.setValue(info.event.extendedProps.numeroSalle);
                    window.comboboxMatiere.setValue(info.event.extendedProps.codeMatiere);

                    // Mettre à jour les champs de texte
                    document.getElementById('numeroEns').value = info.event.extendedProps.nomEnseignant;
                    document.getElementById('numeroSalle').value = info.event.extendedProps.nomSalle;
                    document.getElementById('codeMatiere').value = info.event.extendedProps.nomMatiere;

                    // Sélectionner les groupes
                    const groupesSelect = document.getElementById('groupes');
                    if (info.event.extendedProps.groupes) {
                        const groupeIds = info.event.extendedProps.groupes.map(g => g.id);
                        Array.from(groupesSelect.options).forEach(option => {
                            option.selected = groupeIds.includes(parseInt(option.value));
                        });
                    }

                    document.getElementById('remove').style.display = 'block';
                    modal.show();
                });
            },

            eventDrop: function(info) {
                if (!isAdmin) {
                    info.revert();
                    return;
                }

                const droppedDate = info.event.start;
                const dayOfWeek = droppedDate.getDay();

                if (dayOfWeek === 0 || dayOfWeek === 6) {
                    showAlert('Les séances ne peuvent pas être déplacées au weekend');
                    info.revert();
                    return;
                }

                const seance = {
                    date: info.event.startStr.split('T')[0],
                    heureDebut: info.event.startStr.split('T')[1].slice(0, 5),
                    heureFin: info.event.endStr.split('T')[1].slice(0, 5),
                    numeroEns: info.event.extendedProps.numeroEns,
                    numeroSalle: info.event.extendedProps.numeroSalle,
                    codeMatiere: info.event.extendedProps.codeMatiere
                };

                updateSeance(info.event.id, seance).catch(error => {
                    console.error('Erreur lors de la mise à jour:', error);
                    info.revert();
                });
            }
        });

        calendar.render();

        // Configuration des écouteurs d'événements
        setupEventListeners();
        loadSeances();

        function setupEventListeners() {
            document.getElementById('remove').addEventListener('click', function(e) {
                e.preventDefault();
                if (!currentSeance || !currentSeance.id) return;

                if (confirm('Êtes-vous sûr de vouloir supprimer cette séance ?')) {
                    deleteSeance(currentSeance.id)
                        .then(() => {
                            modal.hide();
                            loadSeances();
                        })
                        .catch(error => {
                            console.error('Erreur lors de la suppression:', error);
                            showAlert('Erreur lors de la suppression de la séance');
                        });
                }
            });

            // document.getElementById('saveSeance').addEventListener('click', function() {
            //     const formData = new FormData(document.getElementById('seanceForm'));
            //     const date = formData.get('date');
            //     const dayOfWeek = new Date(date).getDay();
            //
            //     if (dayOfWeek === 0 || dayOfWeek === 6) {
            //         alert('Les séances ne peuvent pas être créées le weekend');
            //         return;
            //     }
            //
            //     if (formData.get('heureDebut') >= formData.get('heureFin')) {
            //         alert("L'heure de début ne peut pas être supérieure ou égale à l'heure de fin");
            //         return;
            //     }
            //
            //     const seance = {
            //         date: formData.get('date'),
            //         heureDebut: formData.get('heureDebut'),
            //         heureFin: formData.get('heureFin'),
            //         numeroEns: parseInt(formData.get('numeroEns')),
            //         numeroSalle: parseInt(formData.get('numeroSalle')),
            //         codeMatiere: parseInt(formData.get('codeMatiere'))
            //     };
            //
            //     const promise = currentSeance
            //         ? updateSeance(currentSeance.id, seance)
            //         : createSeance(seance);
            //
            //     promise
            //         .then(() => {
            //             modal.hide();
            //             loadSeances();
            //         })
            //         .catch(error => {
            //             console.error('Erreur:', error);
            //             alert('Erreur lors de l\'opération');
            //         });
            // });


            // Gestionnaires des filtres
            document.getElementById('filterEnseignant').addEventListener('change', applyFilters);
            document.getElementById('filterSalle').addEventListener('change', applyFilters);
            document.getElementById('filterMatiere').addEventListener('change', applyFilters);
        }

        function loadSeances() {
            fetch('/api/seances')
                .then(response => {
                    if (!response.ok) {
                        throw new Error(response.status === 401 ? 'Non autorisé' : 'Erreur serveur');
                    }
                    return response.json();
                })
                .then(data => {
                    console.log('Données reçues:', data); // Pour le débogage

                    // Mise à jour du nom de l'utilisateur
                    if (data.nomComplet) {
                        const userNameElements = document.querySelectorAll('.user-name');
                        userNameElements.forEach(element => {
                            element.textContent = data.nomComplet;
                        });

                        // Mise à jour du nom dans le modal de profil
                        const profileNameElement = document.querySelector('.profile-name');
                        if (profileNameElement) {
                            profileNameElement.textContent = data.nomComplet;
                        }

                        // Mise à jour de l'avatar
                        const avatarElements = document.querySelectorAll('.user-avatar span');
                        avatarElements.forEach(element => {
                            element.textContent = data.nomComplet.charAt(0).toUpperCase();
                        });
                    }

                    // Traitement des séances
                    if (data.seances) {
                        allSeances = data.seances;
                        updateFiltersOptions(data.seances);
                        displaySeances(data.seances);
                    }
                })
                .catch(error => {
                    console.error('Erreur:', error);
                    const calendar = document.getElementById('calendar');
                    if (calendar) {
                        calendar.innerHTML = `
                    <div class="alert alert-danger" role="alert">
                        <h4 class="alert-heading">Erreur de chargement</h4>
                        <p>${error.message}</p>
                        <hr>
                        <p class="mb-0">Veuillez réessayer plus tard ou contacter l'administrateur.</p>
                    </div>
                `;
                    }
                });
        }
        function loadEnseignants() {
            return fetch('/api/enseignants')
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Erreur lors de la récupération des enseignants');
                    }
                    return response.json();
                })
                .then(enseignants => {
                    const select = document.getElementById('numeroEns');
                    select.innerHTML = '<option value="">Sélectionner un enseignant</option>';
                    enseignants.forEach(enseignant => {
                        const option = document.createElement('option');
                        option.value = enseignant.numero_ens;
                        option.textContent = enseignant.nom_complet;
                        select.appendChild(option);
                    });
                })
                .catch(error => {
                    console.error('Erreur:', error);
                    addNotification('Erreur lors du chargement des enseignants', 'error');
                });
        }

        function loadSalles() {
            return fetch('/api/salles')
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Erreur lors de la récupération des salles');
                    }
                    return response.json();
                })
                .then(salles => {
                    const select = document.getElementById('numeroSalle');
                    select.innerHTML = '<option value="">Sélectionner une salle</option>';
                    salles.forEach(salle => {
                        const option = document.createElement('option');
                        option.value = salle.numero_salle;
                        option.textContent = salle.nom_salle;
                        select.appendChild(option);
                    });
                })
                .catch(error => {
                    console.error('Erreur:', error);
                    addNotification('Erreur lors du chargement des salles', 'error');
                });
        }

        function loadMatieres() {
            return fetch('/api/matieres')
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Erreur lors de la récupération des matières');
                    }
                    return response.json();
                })
                .then(matieres => {
                    const select = document.getElementById('codeMatiere');
                    select.innerHTML = '<option value="">Sélectionner une matière</option>';
                    matieres.forEach(matiere => {
                        const option = document.createElement('option');
                        option.value = matiere.code_matiere;
                        option.textContent = matiere.nom_matiere;
                        select.appendChild(option);
                    });
                })
                .catch(error => {
                    console.error('Erreur:', error);
                    addNotification('Erreur lors du chargement des matières', 'error');
                });
        }


        function createCombobox(inputId, dropdownId, data, displayKey, valueKey) {
            const input = document.getElementById(inputId);
            const dropdown = document.getElementById(dropdownId);
            let selectedValue = '';
            let items = [];

            function filterItems(query) {
                return data.filter(item =>
                    item[displayKey].toLowerCase().includes(query.toLowerCase())
                );
            }

            function updateDropdown(filteredItems) {
                dropdown.innerHTML = '';
                items = filteredItems;

                filteredItems.forEach((item, index) => {
                    const option = document.createElement('div');
                    option.className = 'combobox-option';
                    option.textContent = item[displayKey];
                    if(item[valueKey] === selectedValue) {
                        option.classList.add('selected');
                    }

                    option.addEventListener('click', () => {
                        input.value = item[displayKey];
                        selectedValue = item[valueKey];
                        input.dataset.value = selectedValue;
                        dropdown.classList.remove('show');
                    });

                    dropdown.appendChild(option);
                });
            }

            input.addEventListener('focus', () => {
                updateDropdown(data);
                dropdown.classList.add('show');
            });

            input.addEventListener('input', () => {
                const filteredItems = filterItems(input.value);
                updateDropdown(filteredItems);
                dropdown.classList.add('show');
            });

            document.addEventListener('click', (e) => {
                if (!input.contains(e.target) && !dropdown.contains(e.target)) {
                    dropdown.classList.remove('show');
                }
            });

            // Pour définir une valeur programmatiquement
            return {
                setValue: (value) => {
                    const item = data.find(i => i[valueKey] === value);
                    if (item) {
                        input.value = item[displayKey];
                        selectedValue = item[valueKey];
                        input.dataset.value = selectedValue;
                    }
                },
                getValue: () => selectedValue
            };
        }

// Dans la fonction loadAllSelects, modifiez le code pour utiliser les combobox :
        // Modification de la fonction loadAllSelects pour inclure les groupes
        function loadAllSelects() {
            return Promise.all([
                fetch('/api/seance/enseignants').then(r => r.json()),
                fetch('/api/salles').then(r => r.json()),
                fetch('/api/matieres').then(r => r.json()),
                fetch('/api/groupes').then(r => r.json())
            ]).then(([enseignants, salles, matieres, groupes]) => {
                window.comboboxEns = createCombobox(
                    'numeroEns',
                    'numeroEnsDropdown',
                    enseignants,
                    'nom_complet',
                    'numero_ens'
                );

                window.comboboxSalle = createCombobox(
                    'numeroSalle',
                    'numeroSalleDropdown',
                    salles,
                    'nom_salle',
                    'numero_salle'
                );

                window.comboboxMatiere = createCombobox(
                    'codeMatiere',
                    'codeMatiereDropdown',
                    matieres,
                    'nom_matiere',
                    'code_matiere'
                );

                // Mise à jour du select des groupes
                const groupesSelect = document.getElementById('groupes');
                groupesSelect.innerHTML = '';
                groupes.forEach(groupe => {
                    const option = document.createElement('option');
                    option.value = groupe.id;
                    option.textContent = `${groupe.nom} (${groupe.anneeScolaire})`;
                    groupesSelect.appendChild(option);
                });
            });
        }



        function updateUserProfile(nomComplet) {
            // Mettre à jour le nom dans l'avatar
            const avatarSpan = document.querySelector('.user-avatar span');
            if (avatarSpan && nomComplet) {
                avatarSpan.textContent = nomComplet.charAt(0).toUpperCase();
            }

            // Mettre à jour le nom dans le modal de profil
            const profileName = document.querySelector('.profile-name');
            if (profileName) {
                profileName.textContent = nomComplet || 'Utilisateur';
            }
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

            updateSelectOptions('filterEnseignant', enseignants);
            updateSelectOptions('filterSalle', salles);
            updateSelectOptions('filterMatiere', matieres);
        }

        function updateSelectOptions(selectId, options) {
            const select = document.getElementById(selectId);
            const currentValue = select.value;

            select.innerHTML = '<option value="">Tous</option>';
            Array.from(options).sort().forEach(option => {
                const optionElement = document.createElement('option');
                optionElement.value = option;
                optionElement.textContent = option;
                select.appendChild(optionElement);
            });

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

        // Modification de la fonction displaySeances pour inclure les groupes
        function displaySeances(seances) {
            calendar.removeAllEvents();
            seances.forEach(seance => {
                const groupesInfo = seance.groupes ?
                    '\nGroupes: ' + seance.groupes.map(g => g.nom).join(', ') : '';

                calendar.addEvent({
                    id: seance.idSeance,
                    title: `${seance.nomMatiere}\n${seance.nomEnseignant}\nSalle: ${seance.nomSalle}${groupesInfo}`,
                    start: `${seance.date}T${seance.heureDebut}`,
                    end: `${seance.date}T${seance.heureFin}`,
                    backgroundColor: getEventColor(seance.nomMatiere),
                    extendedProps: {
                        numeroEns: seance.numeroEns,
                        numeroSalle: seance.numeroSalle,
                        codeMatiere: seance.codeMatiere,
                        nomEnseignant: seance.nomEnseignant,
                        nomMatiere: seance.nomMatiere,
                        nomSalle: seance.nomSalle,
                        groupes: seance.groupes
                    }
                });
            });
        }

        function getEventColor(matiere) {
            return couleursMatieres[matiere] || "#808080";
        }
        // Dans la fonction createSeance du fichier JavaScript
        function loadGroupes() {
            return fetch('/api/groupes')
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Erreur lors de la récupération des groupes');
                    }
                    return response.json();
                })
                .then(groupes => {
                    const select = document.getElementById('groupes');
                    select.innerHTML = '';
                    groupes.forEach(groupe => {
                        const option = document.createElement('option');
                        option.value = groupe.id;
                        option.textContent = `${groupe.nom} (${groupe.anneeScolaire})`;
                        select.appendChild(option);
                    });
                })
                .catch(error => {
                    console.error('Erreur:', error);
                    addNotification('Erreur lors du chargement des groupes', 'error');
                });
        }

// Modification de la fonction createSeance pour inclure les groupes
        function createSeance(seance) {
            return fetch('/api/seances', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(seance)
            }).then(response => {
                if (!response.ok) {
                    if (response.status === 401) {
                        window.location.href = '/login';
                        throw new Error('Session expirée');
                    }
                    return response.json().then(data => {
                        throw new Error(data.error || 'Erreur lors de la création de la séance');
                    });
                }
                return response.json();
            });
        }

// Modification de la fonction updateSeance pour inclure les groupes
        function updateSeance(id, seance) {
            const selectedGroupes = Array.from(document.getElementById('groupes').selectedOptions)
                .map(option => parseInt(option.value));

            seance.groupeIds = selectedGroupes;

            return fetch(`/api/seances/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(seance)
            }).then(response => {
                if (!response.ok) {
                    return response.text().then(text => {
                        throw new Error(text || 'Erreur lors de la mise à jour de la séance');
                    });
                }
                return response.json();
            });
        }

// Mettre à jour le gestionnaire d'événements pour le bouton de sauvegarde
        document.getElementById('saveSeance').addEventListener('click', function() {
            const form = document.getElementById('seanceForm');
            const formData = new FormData(form);

            // Vérification si des modifications ont été apportées
            const hasChanges =
                formData.get('date') !== form.getAttribute('data-original-date') ||
                formData.get('heureDebut') !== form.getAttribute('data-original-heure-debut') ||
                formData.get('heureFin') !== form.getAttribute('data-original-heure-fin') ||
                formData.get('numeroEns') !== form.getAttribute('data-original-numero-ens') ||
                formData.get('numeroSalle') !== form.getAttribute('data-original-numero-salle') ||
                formData.get('codeMatiere') !== form.getAttribute('data-original-code-matiere');

            if (!hasChanges && currentSeance) {
                modal.hide();
                return;
            }

            const date = formData.get('date');
            const dayOfWeek = new Date(date).getDay();

            if (dayOfWeek === 0 || dayOfWeek === 6) {
                alert('Les séances ne peuvent pas être créées le weekend');
                return;
            }

            if (formData.get('heureDebut') >= formData.get('heureFin')) {
                alert("L'heure de début ne peut pas être supérieure ou égale à l'heure de fin");
                return;
            }


            const seance = {
                date: formData.get('date'),
                heureDebut: formData.get('heureDebut'),
                heureFin: formData.get('heureFin'),
                numeroEns: parseInt(document.getElementById('numeroEns').dataset.value),
                numeroSalle: parseInt(document.getElementById('numeroSalle').dataset.value),
                codeMatiere: parseInt(document.getElementById('codeMatiere').dataset.value),
                groupeIds: Array.from(document.getElementById('groupes').selectedOptions)
                    .map(option => parseInt(option.value))
            };

            const promise = currentSeance
                ? updateSeance(currentSeance.id, seance)
                : createSeance(seance);

            promise
                .then(() => {
                    modal.hide();
                    loadSeances();
                    addNotification('Séance enregistrée avec succès', 'success');
                })
                .catch(error => {
                    console.error('Erreur:', error);
                    try {
                        const errorObj = JSON.parse(error.message);
                        alert(errorObj.error || 'Une erreur est survenue');
                    } catch (e) {
                        alert(error.message || 'Une erreur est survenue');
                    }
                });
        });


        // function createSeance(seance) {
        //     return fetch('/api/seances', {
        //         method: 'POST',
        //         headers: {
        //             'Content-Type': 'application/json',
        //         },
        //         body: JSON.stringify(seance)
        //     }).then(response => {
        //         if (!response.ok) throw new Error('Erreur lors de la création de la séance');
        //         return response.json();
        //     });
        // }
        //
        // function updateSeance(id, seance) {
        //     return fetch(`/api/seances/${id}`, {
        //         method: 'PUT',
        //         headers: {
        //             'Content-Type': 'application/json',
        //         },
        //         body: JSON.stringify(seance)
        //     }).then(response => {
        //         if (!response.ok) throw new Error('Erreur lors de la mise à jour de la séance');
        //         return response.json();
        //     });
        // }

        function deleteSeance(id) {
            return fetch(`/api/seances/${id}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                }
            }).then(response => {
                if (!response.ok) throw new Error('Erreur lors de la suppression de la séance');
            });
        }


        updateDashboardStats();
        setInterval(updateDashboardStats, 300000);


        // Mise à jour périodique des statistiques (toutes les 5 minutes)
        setInterval(updateDashboardStats, 300000);

        // Gestionnaire du bouton de notifications
        const notificationButton = document.getElementById('notificationButton');
        const notificationDropdown = document.getElementById('notificationDropdown');

        notificationButton.addEventListener('click', function(e) {
            e.stopPropagation();
            notificationDropdown.classList.toggle('show');
            // Marquer toutes les notifications comme lues
            markNotificationsAsRead();
        });

        function markNotificationsAsRead() {
            notifications.forEach(notification => notification.read = true);
            updateNotificationBadge();
            updateNotificationList();
        }

        // Fermer le dropdown au clic en dehors
        document.addEventListener('click', function(e) {
            if (!notificationDropdown.contains(e.target) && !notificationButton.contains(e.target)) {
                notificationDropdown.classList.remove('show');
            }
        });
        if (!/*[[${session.isAdmin}]]*/ false) {
            // D'abord récupérer le numéro d'enseignant à partir de l'ID du personnel
            fetch('/api/enseignants/me')
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Erreur lors de la récupération des informations de l\'enseignant');
                    }
                    return response.json();
                })
                .then(enseignant => {
                    const numeroEns = enseignant.numero_ens;
                    if (!numeroEns) {
                        throw new Error('Numéro d\'enseignant non trouvé');
                    }

                    // Stocker le numéro d'enseignant pour une utilisation ultérieure
                    window.numeroEnseignant = numeroEns;

                    // Charger les disponibilités initiales
                    loadDisponibilites(numeroEns);

                    // Configurer le gestionnaire d'événements du formulaire
                    document.getElementById('disponibiliteForm').addEventListener('submit', function(e) {
                        e.preventDefault();
                        const checkboxes = document.querySelectorAll('input[name="jours"]:checked');
                        const jours = Array.from(checkboxes).map(cb => cb.value);

                        // Mettre à jour les disponibilités avec le numéro d'enseignant correct
                        updateDisponibilites(numeroEns, jours);
                    });
                })
                .catch(error => {
                    console.error('Erreur:', error);
                    addNotification('Erreur lors de la récupération des informations de l\'enseignant', 'error');
                });

            function updateDisponibilites(numeroEns, jours) {
                // Supprimer les disponibilités existantes
                fetch(`/api/disponibilites/enseignant/${numeroEns}`)
                    .then(response => {
                        if (!response.ok) {
                            throw new Error(`HTTP error! status: ${response.status}`);
                        }
                        return response.json();
                    })
                    .then(disponibilites => {
                        if (!Array.isArray(disponibilites)) {
                            disponibilites = [];
                        }

                        // Supprimer toutes les disponibilités existantes
                        const deletePromises = disponibilites.map(dispo =>
                            fetch(`/api/disponibilites/enseignant/${numeroEns}/jour/${dispo.jour}`, {
                                method: 'DELETE'
                            }).then(response => {
                                if (!response.ok) {
                                    throw new Error(`Erreur lors de la suppression: ${response.status}`);
                                }
                            })
                        );

                        return Promise.all(deletePromises);
                    })
                    .then(() => {
                        // Créer les nouvelles disponibilités
                        const createPromises = jours.map(jour =>
                            fetch('/api/disponibilites', {
                                method: 'POST',
                                headers: {
                                    'Content-Type': 'application/json'
                                },
                                body: JSON.stringify({
                                    numeroEns: numeroEns,
                                    jour: jour
                                })
                            }).then(response => {
                                if (!response.ok) {
                                    throw new Error(`Erreur lors de la création: ${response.status}`);
                                }
                                return response.json();
                            })
                        );

                        return Promise.all(createPromises);
                    })
                    .then(() => {
                        addNotification('Disponibilités mises à jour avec succès', 'success');
                        loadDisponibilites(numeroEns);
                    })
                    .catch(error => {
                        console.error('Erreur:', error);
                        addNotification(`Erreur lors de la mise à jour des disponibilités: ${error.message}`, 'error');
                    });
            }

            function loadDisponibilites(numeroEns) {
                fetch(`/api/disponibilites/enseignant/${numeroEns}`)
                    .then(response => {
                        if (!response.ok) {
                            throw new Error(`HTTP error! status: ${response.status}`);
                        }
                        return response.json();
                    })
                    .then(disponibilites => {
                        if (!Array.isArray(disponibilites)) {
                            disponibilites = [];
                        }

                        // Mettre à jour les cases à cocher
                        document.querySelectorAll('input[name="jours"]').forEach(checkbox => {
                            checkbox.checked = disponibilites.some(d => d.jour === checkbox.value);
                        });
                    })
                    .catch(error => {
                        console.error('Erreur lors du chargement des disponibilités:', error);
                        addNotification(`Erreur lors du chargement des disponibilités: ${error.message}`, 'error');
                    });
            }
        }


        // Exemple de notification
        setTimeout(() => {
            addNotification('Bienvenue dans votre emploi du temps !', 'info');
        }, 2000);
    });

