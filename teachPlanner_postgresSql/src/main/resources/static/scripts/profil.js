// Gestionnaire du menu de profil
document.getElementById('profileTrigger').addEventListener('click', function(e) {
    e.stopPropagation();
    const dropdown = document.getElementById('profileDropdown');
    dropdown.classList.toggle('show');
});

// Fermer le menu au clic en dehors
document.addEventListener('click', function(e) {
    const dropdown = document.getElementById('profileDropdown');
    if (!dropdown.contains(e.target) && !e.target.closest('#profileTrigger')) {
        dropdown.classList.remove('show');
    }
});

// Gestionnaire du thème
let isDarkMode = false;
document.getElementById('themeToggle').addEventListener('click', function() {
    isDarkMode = !isDarkMode;
    document.body.setAttribute('data-theme', isDarkMode ? 'dark' : 'light');
    this.innerHTML = isDarkMode ?
        '<i class="fas fa-sun"></i> Thème clair' :
        '<i class="fas fa-moon"></i> Thème sombre';
});

// Animation de l'avatar
const avatar = document.querySelector('.profile-avatar');
avatar.addEventListener('mouseover', function() {
    this.style.transform = 'scale(1.05) rotate(5deg)';
});
avatar.addEventListener('mouseout', function() {
    this.style.transform = 'scale(1) rotate(0deg)';
});