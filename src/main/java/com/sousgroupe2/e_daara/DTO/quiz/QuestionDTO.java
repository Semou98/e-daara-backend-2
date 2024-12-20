package com.sousgroupe2.e_daara.DTO.quiz;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.sousgroupe2.e_daara.entities.evaluations.Question;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
@AllArgsConstructor @Builder
public class QuestionDTO {
    private Long id;
    private String type;

    @NotNull(message = "Le nombre de points est obligatoire")
    @Positive(message = "Le nombre de points doit être positif")
    private BigDecimal points;

    @NotBlank(message = "Le texte de la question est obligatoire")
    private String textQuestion;

    @NotEmpty(message = "Au moins une réponse est requise")
    private List<ReponseDTO> reponses;
}