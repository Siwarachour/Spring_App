package tn.esprit.back.Entities.coursCertificat;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;


@Entity
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String description;
    private double score;

    /// Questions et choix (ex: { "Q1": ["A", "B", "C"], "Q2": ["X", "Y"] })
    @ElementCollection
    @CollectionTable(name = "test_questions", joinColumns = @JoinColumn(name = "test_id"))
    @Column(name = "question_text")
    private List<String> questions;  // Texte des questions

    @ElementCollection
    @CollectionTable(name = "test_choices", joinColumns = @JoinColumn(name = "test_id"))
    @Column(name = "choices")
    private List<String> choices;  // Tous les choix (regroupés par question)

    @ElementCollection
    @CollectionTable(name = "test_correct_answers", joinColumns = @JoinColumn(name = "test_id"))
    @Column(name = "correct_index")
    private List<Integer> correctAnswerIndices;  // Indices des bonnes réponses

    @OneToOne(mappedBy = "test")
    //@JsonIgnore  // Ignorer la référence inverse
    @JsonBackReference
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String contenu) {
        this.description = contenu;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }

    public List<Integer> getCorrectAnswers() {
        return correctAnswerIndices;
    }

    public void setCorrectAnswers(List<Integer> correctAnswers) {
        this.correctAnswerIndices = correctAnswers;
    }
}
