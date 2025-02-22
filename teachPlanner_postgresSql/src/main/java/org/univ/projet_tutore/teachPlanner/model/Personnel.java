package org.univ.projet_tutore.teachPlanner.model;

import jakarta.persistence.*;
import org.univ.projet_tutore.teachPlanner.service.PasswordHasher;

@Entity
@Table(name = "Personnels", schema = "public")
public class Personnel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idperso;

    @Column(nullable = false, unique = true)
    private String mail;

    @Column(nullable = false)
    private String mdp;

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

}
