// Fonction pour charger les matières à partir de l'API REST
function loadMatieres() {
    const tableBody = document.getElementById('matiere-table');

    fetch('/view/matieres')
        .then(response => {
            if (!response.ok) {
                throw new Error("Erreur HTTP : " + response.status);
            }
            return response.json();
        })
        .then(data => {
            console.log(" Données reçues :", data);

            // Vider le tableau avant de le remplir
            tableBody.innerHTML = "";

            // Ajouter chaque matière au tableau
            data.forEach(matiere => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${matiere.codeMatiere}</td>
                    <td>${matiere.nomMatiere}</td>
                    <td>
                        <button class="btn btn-warning btn-sm me-2" onclick="openEditModal(${matiere.codeMatiere}, '${matiere.nomMatiere}')">
                            Modifier
                        </button>
                        <button class="btn btn-danger btn-sm" onclick="deleteMatiere(${matiere.codeMatiere})">
                            Supprimer
                        </button>
                    </td>
                `;
                tableBody.appendChild(row);
            });
        })
        .catch(error => console.error(" Erreur lors du chargement des matières :", error));
}

// Charger les matières une fois la page chargée
document.addEventListener('DOMContentLoaded', () => {
    loadMatieres();
    document.getElementById('search-input').addEventListener('input', searchTable);
});

// Fonction pour ouvrir la modale de modification
function openEditModal(id, name) {
    document.getElementById('edit-matiere-id').value = id;
    document.getElementById('edit-matiere-id').disabled = true;
    document.getElementById('edit-matiere-name').value = name;

    const editModal = new bootstrap.Modal(document.getElementById('editMatiereModal'));
    editModal.show();
}
window.openEditModal = openEditModal;

// Fonction pour supprimer une matière
function deleteMatiere(id) {
    if (!confirm("Êtes-vous sûr de vouloir supprimer cette matière ?")) {
        return;
    }

    fetch(`/view/matieres/${id}`, {
        method: 'DELETE',
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Erreur lors de la suppression");
            }
            loadMatieres();
        })
        .catch(error => {
            console.error("Erreur lors de la suppression :", error);
            alert("Une erreur est survenue lors de la suppression.");
        });
}
window.deleteMatiere = deleteMatiere;

// Fonction pour enregistrer les modifications d'une matière
document.getElementById('save-matiere-changes').addEventListener('click', () => {
    const id = document.getElementById('edit-matiere-id').value;
    const name = document.getElementById('edit-matiere-name').value.trim();

    if (!name) {
        alert("Le nom de la matière ne peut pas être vide.");
        return;
    }

    fetch(`/view/matieres/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ codeMatiere: id, nomMatiere: name }),
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Erreur lors de la mise à jour.");
            }
            return response.json();
        })
        .then(() => {
            const editModal = bootstrap.Modal.getInstance(document.getElementById('editMatiereModal'));
            editModal.hide();
            loadMatieres();
        })
        .catch(error => console.error("Erreur lors de la mise à jour :", error));
});

// Fonction pour rechercher les matières
function searchTable() {
    const input = document.getElementById("search-input").value.toLowerCase();
    const rows = document.querySelectorAll("#matiere-table tr");

    rows.forEach(row => {
        const id = row.querySelector('td:nth-child(1)')?.innerText.toLowerCase() || "";
        const name = row.querySelector('td:nth-child(2)')?.innerText.toLowerCase() || "";

        if (id.includes(input) || name.includes(input)) {
            row.style.display = "";
        } else {
            row.style.display = "none";
        }
    });
}

// Fonction pour trier les matières
function sortTable(columnIndex) {
    const tableBody = document.getElementById("matiere-table");
    const rows = Array.from(tableBody.rows);

    const sortedRows = rows.sort((a, b) => {
        const cellA = a.cells[columnIndex]?.innerText.toLowerCase() || "";
        const cellB = b.cells[columnIndex]?.innerText.toLowerCase() || "";

        return columnIndex === 0 ? parseInt(cellA) - parseInt(cellB) : cellA.localeCompare(cellB);
    });

    sortedRows.forEach(row => tableBody.appendChild(row));
}
window.sortTable = sortTable;
