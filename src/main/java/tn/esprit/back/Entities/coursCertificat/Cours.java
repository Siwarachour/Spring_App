package tn.esprit.back.Entities.coursCertificat;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import tn.esprit.back.Entities.User.User;

import java.util.Set;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cours {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String titre;
    private String description;
    private String auteur;
    private String categorie;
    private String langue;
    private int duree;
    private double prix;

    private String imageUrl;   // Ajout du champ pour l'image
    private String documentUrl; // Ajout du champ pour le PDF

    @Enumerated(EnumType.STRING)
    private NiveauCours niveau;

    @OneToOne(cascade = CascadeType.ALL)
    private Certificat certificat;

    @OneToOne(cascade = CascadeType.ALL)
    @JsonManagedReference
    private Test test;

    @ManyToMany(mappedBy="cours", cascade = CascadeType.ALL)
    private Set<User> users;

    public Certificat getCertificat() {
        return certificat;
    }


    public void setCertificat(Certificat certificat) {
        this.certificat = certificat;
    }

    public NiveauCours getNiveau() {
        return niveau;
    }

    public void setNiveau(NiveauCours niveau) {
        this.niveau = niveau;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getLangue() {
        return langue;
    }

    public void setLangue(String langue) {
        this.langue = langue;
    }

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Test getTest() {
        return test;
    }

    public void setTest(Test test) {
        this.test = test;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    // Getters et Setters pour les nouveaux champs
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDocumentUrl() {
        return documentUrl;
    }

    public void setDocumentUrl(String documentUrl) {
        this.documentUrl = documentUrl;
    }



}
