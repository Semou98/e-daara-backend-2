package com.sousgroupe2.e_daara.entities.evaluations;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sousgroupe2.e_daara.entities.cours.Contenu;
import com.sousgroupe2.e_daara.entities.cours.Cours;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "quiz")
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Quiz{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idQuiz", nullable = false)
    private Long id;

    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String difficulte;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime dateOuverture;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime dateFermeture;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime delai;

    private Integer nombreQuestion;

    @Column(columnDefinition = "BIGINT")
    private Duration duree;

    private double note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_idCours")
    private Cours cours;

    @OneToMany(mappedBy = "quiz", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<Question> questions = new ArrayList<>();

    public void addQuestion(Question question) {
        questions.add(question);
        question.setQuiz(this);
    }

    public void removeQuestion(Question question) {
        questions.remove(question);
        question.setQuiz(null);
    }
}
