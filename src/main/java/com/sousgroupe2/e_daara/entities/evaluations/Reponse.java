package com.sousgroupe2.e_daara.entities.evaluations;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

@Entity @Table(name = "reponse")
@NoArgsConstructor @AllArgsConstructor @Builder @Getter @Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Reponse {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idReponse", nullable = false)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String optionReponse;

    @Column(columnDefinition = "TINYINT(0)") // 0 : False; 1 : True.
    private Boolean estCorrecte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_idQuestion")
    private Question question;

    public void setQuestion(Question question) {
        this.question = question;
        if (question != null && !question.getReponses().contains(this)) {
            question.getReponses().add(this);
        }
    }
}
