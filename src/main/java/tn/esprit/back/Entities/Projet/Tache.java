package tn.esprit.back.Entities.Projet;

import jakarta.persistence.*;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Entities.enums.Status;

import java.time.LocalDate;

@Entity
public class Tache {
    @Id
    @GeneratedValue
    private int idTache;
    private String nomTache;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    @ManyToOne
    private Projet projet;

    @ManyToOne
    private User utilisateur; // Assignée lors de participation

    @Enumerated(EnumType.STRING)
    private Status status = Status.NOT_BEGIN;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getIdTache() {
        return idTache;
    }

    public void setIdTache(int idTache) {
        this.idTache = idTache;
    }

    public String getNomTache() {
        return nomTache;
    }

    public void setNomTache(String nomTache) {
        this.nomTache = nomTache;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public Projet getProjet() {
        return projet;
    }

    public void setProjet(Projet projet) {
        this.projet = projet;
    }

    public User getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(User utilisateur) {
        this.utilisateur = utilisateur;
    }
}

