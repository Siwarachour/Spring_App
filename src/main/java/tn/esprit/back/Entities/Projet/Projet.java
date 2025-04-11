package tn.esprit.back.Entities.Projet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import tn.esprit.back.Entities.User.User;
import tn.esprit.back.Entities.enums.Status;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Projet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int idProjet;
    private String nomProjet;
    private String description;
    private int nbreGestions; // max taches
    private int nbreMembreDisponible = 0;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "createur_id")
    private User createur;

    @ManyToMany
    private List<User> membres = new ArrayList<>();

    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL)
    private List<Tache> taches = new ArrayList<>();


    @Enumerated(EnumType.STRING)
    private Status status = Status.NOT_BEGIN;




    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getIdProjet() {
        return idProjet;
    }

    public void setIdProjet(int idProjet) {
        this.idProjet = idProjet;
    }

    public List<Tache> getTaches() {
        return taches;
    }

    public void setTaches(List<Tache> taches) {
        this.taches = taches;
    }

    public List<User> getMembres() {
        return membres;
    }

    public void setMembres(List<User> membres) {
        this.membres = membres;
    }

    @JsonProperty("createurNom")
    public String getCreateurNom() {
        return createur != null ? createur.getUsername() : null; // Retourner le nom du créateur
    }
    public void setCreateur(User createur) {
        this.createur = createur;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public int getNbreMembreDisponible() {
        return nbreMembreDisponible;
    }

    public void setNbreMembreDisponible(int nbreMembreDisponible) {
        this.nbreMembreDisponible = nbreMembreDisponible;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNomProjet() {
        return nomProjet;
    }

    public void setNomProjet(String nomProjet) {
        this.nomProjet = nomProjet;
    }

    public int getNbreGestions() {
        return nbreGestions;
    }

    public void setNbreGestions(int nbreGestions) {
        this.nbreGestions = nbreGestions;
    }
}
