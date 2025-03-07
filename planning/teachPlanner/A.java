<!DOCTYPE html>
<html lang="fr" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestion des Personnels - UT2J</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        :root {
            --ut2j-blue: #003366;
            --ut2j-light-blue: #004d99;
        }

        body {
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            font-family: 'Arial', sans-serif;
            min-height: 100vh;
        }

        .container {
            margin-top: 50px;
            margin-bottom: 50px;
        }

        .page-title {
            color: var(--ut2j-blue);
            margin-bottom: 30px;
            font-weight: 600;
            text-align: center;
        }

        .form-container {
            background: #ffffff;
            border-radius: 12px;
            padding: 30px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
            margin-bottom: 30px;
            border-top: 5px solid var(--ut2j-blue);
        }

        .form-container h2 {
            font-size: 1.5rem;
            margin-bottom: 20px;
            color: var(--ut2j-blue);
            font-weight: 600;
        }

        .form-label {
            font-weight: 500;
            color: #495057;
        }

        .form-control:focus {
            border-color: var(--ut2j-light-blue);
            box-shadow: 0 0 0 0.25rem rgba(0, 51, 102, 0.25);
        }

        .btn-primary {
            background-color: var(--ut2j-blue);
            border-color: var(--ut2j-blue);
            padding: 10px 20px;
            font-weight: 500;
            transition: all 0.3s ease;
        }

        .btn-primary:hover {
            background-color: var(--ut2j-light-blue);
            border-color: var(--ut2j-light-blue);
            transform: translateY(-2px);
        }

        .btn-outline-secondary {
            color: var(--ut2j-blue);
            border-color: var(--ut2j-blue);
        }

        .btn-outline-secondary:hover {
            background-color: var(--ut2j-blue);
            color: white;
        }

        .alert {
            border-radius: 8px;
            padding: 15px;
            margin-bottom: 20px;
        }

        .icon-container {
            display: flex;
            align-items: center;
            justify-content: center;
            width: 50px;
            height: 50px;
            background-color: var(--ut2j-blue);
            border-radius: 50%;
            margin: 0 auto 20px;
        }

        .icon-container i {
            color: white;
            font-size: 24px;
        }

        .actions-container {
            display: flex;
            justify-content: space-between;
            margin-top: 30px;
        }

        .role-info {
            margin-top: 15px;
            padding: 15px;
            border-radius: 8px;
            background-color: #f8f9fa;
            border-left: 4px solid var(--ut2j-blue);
        }

        .role-info h5 {
            color: var(--ut2j-blue);
            margin-bottom: 10px;
        }

        .role-info p {
            margin-bottom: 5px;
            color: #6c757d;
        }

        .role-specific-fields {
            padding: 15px;
            background-color: #f8f9fa;
            border-radius: 8px;
            margin-bottom: 20px;
            border-left: 4px solid var(--ut2j-blue);
        }
    </style>
</head>
<body>
<div class="container">
    <h1 class="page-title">
        <i class="fas fa-user-plus me-2"></i>Ajouter un Personnel
    </h1>

    <div class="form-container">
        <div class="icon-container">
            <i class="fas fa-user"></i>
        </div>

        <div id="alertContainer"></div>

        <form id="personnelForm">
            <div class="mb-3">
                <label for="mail" class="form-label">
                    <i class="fas fa-envelope me-2"></i>Email
                </label>
                <input type="email" class="form-control" id="mail" required
                       placeholder="exemple@ut2j.fr">
                <div class="form-text">L'email servira d'identifiant de connexion.</div>
            </div>

            <div class="mb-3">
                <label for="mdp" class="form-label">
                    <i class="fas fa-lock me-2"></i>Mot de passe
                </label>
                <div class="input-group">
                    <input type="password" class="form-control" id="mdp" required
                           placeholder="Mot de passe">
                    <button class="btn btn-outline-secondary" type="button" id="togglePassword">
                        <i class="fas fa-eye"></i>
                    </button>
                </div>
                <div class="form-text">Choisissez un mot de passe sécurisé.</div>
            </div>

            <div class="mb-3">
                <label for="nom" class="form-label">
                    <i class="fas fa-user me-2"></i>Nom
                </label>
                <input type="text" class="form-control" id="nom" required
                       placeholder="Nom">
            </div>

            <div class="mb-3">
                <label for="prenom" class="form-label">
                    <i class="fas fa-user me-2"></i>Prénom
                </label>
                <input type="text" class="form-control" id="prenom" required
                       placeholder="Prénom">
            </div>

            <div class="mb-4">
                <label for="role" class="form-label">
                    <i class="fas fa-user-tag me-2"></i>Rôle
                </label>
                <select class="form-select" id="role" required>
                    <option value="" selected disabled>Sélectionnez un rôle</option>
                    <option value="ADMIN">Administrateur</option>
                    <option value="ENSEIGNANT">Enseignant</option>
                    <option value="ETUDIANT">Étudiant</option>
                </select>
            </div>

            <div id="roleInfo" class="role-info d-none">
                <h5 id="roleTitle">Information sur le rôle</h5>
                <p id="roleDescription"></p>
            </div>

            <!-- Champs spécifiques pour chaque rôle -->
            <div id="enseignantFields" class="role-specific-fields d-none">
                <h5><i class="fas fa-chalkboard-teacher me-2"></i>Informations Enseignant</h5>
                <div class="mb-3">
<!--                    <label for="numeroEns" class="form-label">Numéro Enseignant</label>-->
<!--                    <input type="number" class="form-control" id="numeroEns" min="1"-->
<!--                           placeholder="Entrez un numéro unique">-->
<!--                    <div class="form-text">Entrez un numéro qui n'est pas déjà utilisé par un autre enseignant.</div>-->
                </div>
            </div>

            <div id="etudiantFields" class="role-specific-fields d-none">
                <h5><i class="fas fa-user-graduate me-2"></i>Informations Étudiant</h5>
                <div class="mb-3">
                    <label for="numeroEtudiant" class="form-label">Numéro Étudiant</label>
                    <input type="number" class="form-control" id="numeroEtudiant" min="1"
                           placeholder="Entrez un numéro unique">
                    <div class="form-text">Entrez un numéro qui n'est pas déjà utilisé par un autre étudiant.</div>
                </div>
                <div class="mb-3">
                    <label for="groupe" class="form-label">
                        <i class="fas fa-users me-2"></i>Groupe
                    </label>
                    <select class="form-select" id="groupe">
                        <option value="" selected>Sélectionnez un groupe (optionnel)</option>
                    </select>
                </div>
            </div>

            <div id="adminFields" class="role-specific-fields d-none">
                <h5><i class="fas fa-user-shield me-2"></i>Informations Administrateur</h5>
                <div class="mb-3">
                    <label for="idAdmin" class="form-label">ID Administrateur</label>
                    <input type="number" class="form-control" id="idAdmin" min="1"
                           placeholder="Entrez un ID unique">
                    <div class="form-text">Entrez un ID qui n'est pas déjà utilisé par un autre administrateur.</div>
                </div>
            </div>

            <div class="actions-container">
                <a href="/personnels/liste" class="btn btn-outline-secondary">
                    <i class="fas fa-arrow-left me-2"></i>Retour à la liste
                </a>
                <button type="submit" class="btn btn-primary">
                    <i class="fas fa-save me-2"></i>Enregistrer
                </button>
            </div>
        </form>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Gestion de l'affichage/masquage du mot de passe
        const togglePassword = document.getElementById('togglePassword');
        const password = document.getElementById('mdp');

        togglePassword.addEventListener('click', function() {
            const type = password.getAttribute('type') === 'password' ? 'text' : 'password';
            password.setAttribute('type', type);
            this.querySelector('i').classList.toggle('fa-eye');
            this.querySelector('i').classList.toggle('fa-eye-slash');
        });

        // Gestion de l'affichage des informations sur les rôles
        const roleSelect = document.getElementById('role');
        const roleInfo = document.getElementById('roleInfo');
        const roleTitle = document.getElementById('roleTitle');
        const roleDescription = document.getElementById('roleDescription');
        const groupeContainer = document.getElementById('groupe');

        // Champs spécifiques pour chaque rôle
        const enseignantFields = document.getElementById('enseignantFields');
        const etudiantFields = document.getElementById('etudiantFields');
        const adminFields = document.getElementById('adminFields');

        roleSelect.addEventListener('change', function() {
            const selectedRole = this.value;
            roleInfo.classList.remove('d-none');

            // Cacher tous les champs spécifiques
            enseignantFields.classList.add('d-none');
            etudiantFields.classList.add('d-none');
            adminFields.classList.add('d-none');

            switch(selectedRole) {
                case 'ADMIN':
                    roleTitle.textContent = 'Administrateur';
                    roleDescription.textContent = 'Les administrateurs ont accès à toutes les fonctionnalités de gestion du système.';
                    adminFields.classList.remove('d-none');
                    break;

                case 'ENSEIGNANT':

                    roleTitle.textContent = 'Enseignant';
                    roleDescription.textContent = 'Les enseignants peuvent gérer leurs disponibilités et consulter leur emploi du temps.';
                    enseignantFields.classList.remove('d-none');
                    return createEnseignant(nom, prenom, personnel.idperso);

                case 'ETUDIANT':
                    roleTitle.textContent = 'Étudiant';
                    roleDescription.textContent = 'Les étudiants peuvent consulter leur emploi du temps et les informations sur leurs cours.';
                    etudiantFields.classList.remove('d-none');
                    loadGroupes();
                    break;
                default:
                    roleInfo.classList.add('d-none');
            }
        });

        // Chargement des groupes pour les étudiants
        function loadGroupes() {
            fetch('/api/groupes')
                .then(response => response.json())
                .then(groupes => {
                    const groupeSelect = document.getElementById('groupe');
                    groupeSelect.innerHTML = '<option value="" selected>Sélectionnez un groupe (optionnel)</option>';

                    groupes.forEach(groupe => {
                        const option = document.createElement('option');
                        option.value = groupe.id;
                        option.textContent = `${groupe.nom} (${groupe.anneeScolaire})`;
                        groupeSelect.appendChild(option);
                    });
                })
                .catch(error => {
                    console.error('Erreur lors du chargement des groupes:', error);
                    showAlert('Erreur lors du chargement des groupes', 'danger');
                });
        }

        // Gestion de la soumission du formulaire
        const form = document.getElementById('personnelForm');

        form.addEventListener('submit', function(e) {
            e.preventDefault();

            const role = document.getElementById('role').value;
            const mail = document.getElementById('mail').value;
            const mdp = document.getElementById('mdp').value;
            const nom = document.getElementById('nom').value;
            const prenom = document.getElementById('prenom').value;

            // Vérifier que les champs spécifiques au rôle sont remplis
            // if (role === 'ENSEIGNANT' && !document.getElementById('numeroEns').value) {
            //     showAlert('Veuillez entrer un numéro d\'enseignant', 'danger');
            //     return;
            // }

            if (role === 'ETUDIANT' && !document.getElementById('numeroEtudiant').value) {
                showAlert('Veuillez entrer un numéro d\'étudiant', 'danger');
                return;
            }

            if (role === 'ADMIN' && !document.getElementById('idAdmin').value) {
                showAlert('Veuillez entrer un ID d\'administrateur', 'danger');
                return;
            }

            // Création du personnel
            createPersonnel(mail, mdp, role)
                .then(personnel => {
                    // En fonction du rôle, créer l'entité correspondante
                    switch(role) {
                        case 'ADMIN':
                            const idAdmin = parseInt(document.getElementById('idAdmin').value);
                            return createAdmin(nom, prenom, personnel.idperso, idAdmin);
                        case 'ENSEIGNANT':
                            const numeroEns = parseInt(document.getElementById('numeroEns').value);
                            return createEnseignant(nom, prenom, personnel.idperso, numeroEns);
                        case 'ETUDIANT':
                            const numeroEtudiant = parseInt(document.getElementById('numeroEtudiant').value);
                            const groupeId = document.getElementById('groupe').value;
                            return createEtudiant(nom, prenom, personnel.idperso, groupeId, numeroEtudiant);
                        default:
                            throw new Error('Rôle non reconnu');
                    }
                })
                .then(result => {
                    showAlert(`${roleTitle.textContent} ajouté avec succès !`, 'success');
                    setTimeout(() => {
                        // Redirection en fonction du rôle
                        switch(role) {
                            case 'ADMIN':
                                window.location.href = '/administrateurs/liste';
                                break;
                            case 'ENSEIGNANT':
                                window.location.href = '/enseignants/liste';
                                break;
                            case 'ETUDIANT':
                                window.location.href = '/etudiants/liste';
                                break;
                            default:
                                window.location.href = '/personnels/liste';
                        }
                    }, 2000);
                })
                .catch(error => {
                    console.error('Erreur:', error);
                    showAlert('Erreur lors de la création: ' + error.message, 'danger');
                });
        });

        // Fonctions d'API
        async function createPersonnel(mail, mdp, role) {
            console.log("les donnes recus dans la fonction createPersonnel : ",mail,mdp,role);
            const response = await fetch('/api/personnels', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    mail: mail,
                    mdp: mdp,
                    role: role
                })
            });

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Erreur lors de la création du personnel');
            }

            return response.json();
        }

        async function createAdmin(nom, prenom, idperso, idAdmin) {
            const response = await fetch('/api/administrateurs', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    id: idAdmin, // ID manuel
                    nomAdmin: nom,
                    prenomAdmin: prenom,
                    personnel: {
                        idperso: idperso
                    }
                })
            });

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Erreur lors de la création de l\'administrateur');
            }

            return response.json();
        }

        async function createEnseignant(nom, prenom, idperso, numeroEns) {
            // Ajouter du logging pour déboguer
            console.log("Création enseignant avec données:", { nom, prenom, idperso, numeroEns });

            // Vous pourriez essayer sans spécifier le numeroEns
            const body = {
                nomEns: nom,
                prenomEns: prenom,
                personnel: {
                    idperso: idperso
                }
            };

            // Si vous voulez vraiment le spécifier manuellement
            if (numeroEns) {
                body.numeroEns = numeroEns;
            }

            const response = await fetch('/api/enseignants', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    nomEns: nom,
                    prenomEns: prenom,
                    personnel: {
                        idperso: idperso
                    }
                })
            });

            if (!response.ok) {
                const errorText = await response.text();
                console.error('Réponse brute du serveur:', errorText);
                try {
                    const error = JSON.parse(errorText);
                    throw new Error(error.message || 'Erreur lors de la création de l\'enseignant');
                } catch (e) {
                    throw new Error('Erreur serveur: ' + errorText);
                }
            }

            return response.json();
        }

        async function createEtudiant(nom, prenom, idperso, groupeId, numeroEtudiant) {
            console.log("les donnes recus dans la fonction createPersonnel : ",nom,prenom,idperso,groupeId,numeroEtudiant);
            const body = {
                numeroEtudiant: numeroEtudiant, // Numéro manuel
                nom: nom,
                prenom: prenom,
                personnel: {
                    idperso: idperso
                }
            };

            if (groupeId) {
                body.groupe = {
                    id: groupeId
                };
            }

            const response = await fetch('/api/etudiants', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(body)
            });

            if (!response.ok) {
                const error = await response.json();
                throw new Error(error.message || 'Erreur lors de la création de l\'étudiant');
            }

            return response.json();
        }

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
    });
</script>
</body>
</html>