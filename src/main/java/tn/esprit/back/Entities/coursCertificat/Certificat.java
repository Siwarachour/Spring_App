package tn.esprit.back.Entities.coursCertificat;


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
    private Cours cours;

}
