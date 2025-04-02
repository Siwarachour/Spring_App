package tn.esprit.back.Entities.coursCertificat;


import jakarta.persistence.*;

@Entity
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String contenu;
    private double score;

    @OneToOne(mappedBy = "test")
    private Cours cours;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Cours getCours() {
        return cours;
    }

    public void setCours(Cours cours) {
        this.cours = cours;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }
}
