package com.sousgroupe2.e_daara.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sousgroupe2.e_daara.entities.evaluations.Projet;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
public class ProjetDTO {

    private Long id;

    @NotNull(message = "Le titre est obligatoire")
    private String titre;

    private String description;

    private String difficulte;

    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateOuverture;

    private LocalDateTime dateFermeture;

    private LocalDateTime delai;

    @NotNull(message = "L'URL du fichier est obligatoire")
    private String urlFichier;

    @Min(0) @Max(20)
    private double note;

    private Long coursId;

    public ProjetDTO(Projet projet) {
        this.id = projet.getId();
        this.titre = projet.getTitre();
        this.description = projet.getDescription();
        this.difficulte = projet.getDifficulte();
        this.dateOuverture = projet.getDateOuverture();
        this.dateFermeture = projet.getDateFermeture();
        this.delai = projet.getDelai();
        this.urlFichier = projet.getUrlFichier();
        this.note = projet.getNote();
        this.coursId = projet.getCours()!= null ? projet.getCours().getId(): null;
    }
}