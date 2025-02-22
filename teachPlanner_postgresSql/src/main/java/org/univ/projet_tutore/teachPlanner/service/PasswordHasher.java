package org.univ.projet_tutore.teachPlanner.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordHasher {

    private String hashedPassword;

    // Constructeur vide
    public PasswordHasher() {}

    // Constructeur avec mot de passe pour le hacher
    public PasswordHasher(String mdp) {
        this.hashedPassword = hashPassword(mdp); // Hachage du mot de passe
    }

    // Méthode pour hacher le mot de passe avec SHA-256
    private String hashPassword(String password) {
        try {
            // Création de l'instance de MessageDigest pour SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Hachage du mot de passe
            byte[] hash = digest.digest(password.getBytes());

            // Retourne le résultat encodé en Base64 pour une meilleure lisibilité
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hachage du mot de passe", e);
        }
    }

    // Méthode pour récupérer le mot de passe haché
    public String getHashedPassword() {
        return this.hashedPassword;
    }

    // Méthode main pour tester la classe
//    public static void main(String[] args) {
//        String password = "admin@123";
//
//        // Créer une instance de PasswordHasher avec le mot de passe
//        PasswordHasher hasher = new PasswordHasher(password);
//
//        // Afficher le mot de passe haché
//        System.out.println("Mot de passe haché : " + hasher.getHashedPassword());
//    }
}
