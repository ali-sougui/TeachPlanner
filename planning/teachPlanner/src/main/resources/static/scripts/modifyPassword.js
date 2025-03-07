// Fonction pour basculer entre masquer et afficher le mot de passe
function togglePasswordVisibility(inputId) {
    const input = document.getElementById(inputId);
    if (input.type === "password") {
        input.type = "text";
    } else {
        input.type = "password";
    }
}

// Gestion de la soumission du formulaire
document.getElementById('passwordForm').addEventListener('submit', async function (event) {
    event.preventDefault(); // Empêche la soumission classique du formulaire

    // Récupère les valeurs des champs
    const oldPassword = document.getElementById('oldPassword').value;
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;

    console.log("Old Password:", oldPassword);
    console.log("New Password:", newPassword);
    console.log("Confirm Password:", confirmPassword);

    // Vérifie que les mots de passe correspondent
    if (newPassword !== confirmPassword) {
        console.log("Les mots de passe ne correspondent pas.");
        showMessage("Les nouveaux mots de passe ne correspondent pas.", 'danger');
        return;
    }

    // Envoie les données au serveur
    try {
        const response = await fetch('/api/personnels/modifyPassword', {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                oldPassword: oldPassword,
                newPassword: newPassword,
                confirmPassword: confirmPassword
            })
        });

        const result = await response.json();
        console.log("Réponse serveur:", result);

        if (response.ok) {
            showMessage(result.message || "Mot de passe modifié avec succès.", 'success');
            setTimeout(() => {
                window.location.href = '/seances';
            }, 1000);
        } else {
            showMessage(result.message || "Erreur lors de la modification du mot de passe.", 'danger');
        }
    } catch (error) {
        console.log("Erreur réseau:", error);
        showMessage("Erreur réseau : " + error.message, 'danger');
    }
});


// Fonction pour afficher les messages
function showMessage(message, type) {
    const messageDiv = document.getElementById('message');

    console.log("Message div:", messageDiv); // Vérifie si l'élément est bien récupéré
    if (!messageDiv) return; // Stoppe la fonction si l'élément n'est pas trouvé

    messageDiv.textContent = message;
    messageDiv.className = `alert alert-${type}`;
    messageDiv.style.display = 'block';

    // Cache le message après 3 secondes
    setTimeout(() => {
        messageDiv.style.display = 'none';
    }, 3000);
}