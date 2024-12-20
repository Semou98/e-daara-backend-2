package com.sousgroupe2.e_daara.entities.evaluations;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name = "question")
@NoArgsConstructor @AllArgsConstructor @Getter @Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Question {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idQuestion", nullable = false)
    private Long id;

    private String type;

    @Column(precision = 3, scale = 1, columnDefinition = "DECIMAL(3,1)")
    private BigDecimal points;

    @Column(columnDefinition = "TEXT")
    private String textQuestion;

    @OneToMany(mappedBy = "question", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<Reponse> reponses = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_idQuiz")
    private Quiz quiz;

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
        if (quiz != null && !quiz.getQuestions().contains(this)) {
            quiz.getQuestions().add(this);
        }
    }

    public void addReponse(Reponse reponse) {
        reponses.add(reponse);
        reponse.setQuestion(this);
    }

    public void removeReponse(Reponse reponse) {
        reponses.remove(reponse);
        reponse.setQuestion(null);
    }
}
