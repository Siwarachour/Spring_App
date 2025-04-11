package tn.esprit.back.Entities.coursCertificat;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class Certificat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nom;
    private String instructeur;
    private int validite;
    private int statut;

    @Enumerated(EnumType.STRING)
    private NiveauCertificat niveau;

    @OneToOne(mappedBy = "certificat")
    @JsonBackReference
    private Cours cours;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getInstructeur() {
        return instructeur;
    }

    public void setInstructeur(String instructeur) {
        this.instructeur = instructeur;
    }

    public int getValidite() {
        return validite;
    }

    public void setValidite(int validite) {
        this.validite = validite;
    }

    public int getStatut() {
        return statut;
    }

    public void setStatut(int statut) {
        this.statut = statut;
    }

    public NiveauCertificat getNiveau() {
        return niveau;
    }

    public void setNiveau(NiveauCertificat niveau) {
        this.niveau = niveau;
    }

    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
    }



}
