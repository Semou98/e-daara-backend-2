package com.sousgroupe2.e_daara.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import com.sousgroupe2.e_daara.entities.cours.Cours;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor @AllArgsConstructor
@Data @Getter @Setter  @Builder
public class ParcoursDTO {
    private Long id;
    @NotNull(message = "Le nom du parcours est obligatoire")
    private String nom;
    @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères")
    private String description;
    private String difficulte;
    private String prerequis;
    private String duree;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private BigDecimal progression;
    private byte[] image;
    private String imageType;
    private Long categorieId;
    //private List<CoursDTO> coursIds;
    private List<Long> coursIds;
    private List<Long> utilisateurIds;
}
