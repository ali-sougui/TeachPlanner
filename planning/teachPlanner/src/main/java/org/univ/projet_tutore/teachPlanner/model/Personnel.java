package org.univ.projet_tutore.teachPlanner.model;

import jakarta.persistence.*;
import org.univ.projet_tutore.teachPlanner.service.PasswordHasher;

@Entity
@Table(name = "personnels")
public class Personnel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idperso;

    @Column(nullable = false, unique = true)
    private String mail;

    @Column(nullable = false)
    private String mdp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    public enum Role {
        ADMIN,
        ENSEIGNANT,
        ETUDIANT
    }

    // Getters et Setters
    public Integer getIdperso() {
        return idperso;
    }

    public void setIdperso(Integer idperso) {
        this.idperso = idperso;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        PasswordHasher password = new PasswordHasher(mdp);
        this.mdp = password.getHashedPassword();
    }
    public void setNotMdp(String mdp) {

        this.mdp = mdp;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }
}
