package com.sousgroupe2.e_daara.DTO;

import com.sousgroupe2.e_daara.enums.ERole;
import com.sousgroupe2.e_daara.enums.ESexe;
import com.sousgroupe2.e_daara.enums.EStatut;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Data @NoArgsConstructor @AllArgsConstructor
@Getter @Setter @Builder
public class UtilisateurDTO {
    private Long id;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotNull(message = "Le sexe est obligatoire")
    private ESexe sexe;

    @Email(message = "L'email doit être valide")
    @NotBlank(message = "L'email est obligatoire")
    private String email;

    private String password;
    private String telephone;

    private Set<ERole> roles;
    private EStatut statut;

    private byte[] image;
    private byte[] photo;
    private String typePhoto;

    private LocalDate dateNaissance;
    private String nationalite;
    private String paysResidence;

    private LocalDateTime dateCreation;
    private LocalDateTime dateDerniereModification;
    private List<Long> parcoursIds;
}