package com.sousgroupe2.e_daara.DTO.quiz;

import com.sousgroupe2.e_daara.entities.evaluations.Reponse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReponseDTO {
    private Long id;

    @NotBlank(message = "L'option de réponse est obligatoire")
    private String optionReponse;

    @NotNull(message = "Le statut de la réponse est obligatoire")
    private Boolean estCorrecte;
}