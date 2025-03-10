package tn.esprit.back.Entities.coursCertificat;


import jakarta.persistence.*;

@Entity
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

    @Enumerated(EnumType.STRING)
    private NiveauCours niveau;

    @OneToOne
    private Certificat certificat;
}
