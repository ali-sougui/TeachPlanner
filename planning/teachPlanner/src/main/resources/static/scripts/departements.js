// Fonction pour charger les départements à partir de l'API REST
function loadDepartments() {
    const tableBody = document.getElementById('department-table'); // Sélectionne le tableau
    console.log("Chargement des départements...");

    fetch('/api/departements') // Appelle l'API REST pour récupérer les départements
        .then(response => {
            if (!response.ok) {
                throw new Error("Erreur HTTP : " + response.status);
            }
            return response.json(); // Convertit la réponse en JSON
        })
        .then(data => {
            console.log("Données reçues :", data); // Affiche les données reçues dans la console

            // Vide le tableau avant de le remplir
            tableBody.innerHTML = "";

            // Ajoute chaque département au tableau
            data.forEach(department => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${department.idDepartement}</td>
                    <td>${department.nomDepartement}</td>
                    <td>
                        <button class="btn btn-warning btn-sm me-2" onclick="openEditModal(${department.idDepartement}, '${department.nomDepartement}')">
                           Modifier
                        </button>
                        <button class="btn btn-danger btn-sm" onclick="deleteDepartment(${department.idDepartement})">
                            Supprimer
                        </button>
                    </td>
                `;
                tableBody.appendChild(row); // Ajoute la ligne au tableau
            });
        })
        .catch(error => console.error("Erreur lors du chargement des départements :", error)); // Logue les erreurs en cas d'échec
}

// Attache `loadDepartments` à `DOMContentLoaded` pour charger les départements automatiquement
document.addEventListener('DOMContentLoaded', () => {
    loadDepartments(); // Charge les départements une fois que la page est prête

    // Ajoute l'événement de recherche sur l'input
    document.getElementById('search-input').addEventListener('input', searchTable);
});

// Fonction pour ouvrir la modale avec les informations d'un département
function openEditModal(id, name) {
    console.log("ID reçu:", id);
    console.log("Nom reçu:", name);

    // Remplit les champs du formulaire avec les informations du département
    document.getElementById('edit-department-id').value = id; // Remplit l'ID (non modifiable)
    document.getElementById('edit-department-id').disabled = true; // Désactive le champ ID
    document.getElementById('edit-department-name').value = name; // Remplit le nom

    // Affiche la modale Bootstrap
    const editModal = new bootstrap.Modal(document.getElementById('editDepartmentModal'));
    editModal.show();
    console.log("La modale est ouverte.");
}

// Attache `openEditModal` à l'objet global `window` pour permettre son accès
window.openEditModal = openEditModal;

// Fonction pour supprimer un département
function deleteDepartment(id) {
    if (!confirm("Êtes-vous sûr de vouloir supprimer ce département ?")) {
        return; // Annule la suppression si l'utilisateur refuse
    }

    console.log("Suppression du département avec ID :", id);

    fetch(`/api/departements/${id}`, {
        method: 'DELETE', // Méthode DELETE pour supprimer le département
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Erreur lors de la suppression.");
            }
            console.log("Département supprimé avec succès !");
            loadDepartments(); // Recharge la liste après suppression
            alert("Département supprimé avec succès !");
        })
        .catch(error => {
            console.error("Erreur lors de la suppression :", error);
            alert("Une erreur est survenue lors de la suppression.");
        });
}

// Attache `deleteDepartment` à l'objet global `window`
window.deleteDepartment = deleteDepartment;

// Fonction pour enregistrer les modifications d'un département
document.getElementById('save-department-changes').addEventListener('click', () => {
    const id = document.getElementById('edit-department-id').value; // L'identifiant reste inchangé
    const name = document.getElementById('edit-department-name').value.trim(); // Nouveau Nom

    if (!name) {
        alert("Le nom du département ne peut pas être vide.");
        return; // Stoppe l'enregistrement si le champ Nom est vide
    }

    console.log("Données envoyées :", { idDepartement: id, nomDepartement: name });

    // Requête PUT pour mettre à jour le département
    fetch(`/api/departements/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ idDepartement: id, nomDepartement: name }),
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Erreur lors de la mise à jour.");
            }
            return response.json();
        })
        .then(() => {
            // Ferme la modale
            const editModal = bootstrap.Modal.getInstance(document.getElementById('editDepartmentModal'));
            editModal.hide();

            // Recharge la liste des départements
            loadDepartments();

            alert("Modifications enregistrées avec succès !");
        })
        .catch(error => console.error("Erreur lors de la mise à jour :", error));
});

// Fonction pour rechercher les départements
function searchTable() {
    const input = document.getElementById("search-input").value.toLowerCase(); // Texte entré par l'utilisateur
    const rows = document.querySelectorAll("#department-table tr"); // Toutes les lignes du tableau

    rows.forEach(row => {
        const id = row.querySelector('td:nth-child(1)')?.innerText.toLowerCase() || ""; // Colonne ID
        const name = row.querySelector('td:nth-child(2)')?.innerText.toLowerCase() || ""; // Colonne Nom

        // Vérifie si l'ID ou le Nom contient la valeur recherchée
        if (id.includes(input) || name.includes(input)) {
            row.style.display = ""; // Affiche la ligne
        } else {
            row.style.display = "none"; // Masque la ligne
        }
    });
}

// Fonction pour trier les départements
function sortTable(columnIndex) {
    const tableBody = document.getElementById("department-table");
    const rows = Array.from(tableBody.rows); // Convertit en tableau pour le tri

    const sortedRows = rows.sort((a, b) => {
        const cellA = a.cells[columnIndex]?.innerText.toLowerCase() || "";
        const cellB = b.cells[columnIndex]?.innerText.toLowerCase() || "";

        if (columnIndex === 0) {
            return parseInt(cellA) - parseInt(cellB); // Trie numérique (ID)
        } else {
            return cellA.localeCompare(cellB); // Trie alphabétique (Nom)
        }
    });

    // Réinsère les lignes triées dans le tableau
    sortedRows.forEach(row => tableBody.appendChild(row));
}

// Attache les fonctions de tri au contexte global
window.sortTable = sortTable;
