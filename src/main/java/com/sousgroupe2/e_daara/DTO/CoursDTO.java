package com.sousgroupe2.e_daara.DTO;

import com.sousgroupe2.e_daara.entities.cours.Contenu;
import com.sousgroupe2.e_daara.entities.evaluations.Projet;
import com.sousgroupe2.e_daara.entities.evaluations.Quiz;
import com.sousgroupe2.e_daara.entities.parcours.Parcours;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder @Getter @Setter
public class CoursDTO {
    private Long id;
    private String titre;
    private String description;
    private Boolean isCompleted;
    private List<Long> contenusIds;
    private List<Long> projetsIds;
    private List<Long> quizIds;
    private List<Long> parcoursIds;

    public CoursDTO(Long id) {
        this.id = id;
    }
}