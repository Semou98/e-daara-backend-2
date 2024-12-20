package com.sousgroupe2.e_daara.DTO;

import com.sousgroupe2.e_daara.enums.ETypeContenu;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter @Builder
public class ContenuDTO {
    private Long id;
    private String titre;
    private String description;
    @Enumerated(EnumType.STRING)
    private ETypeContenu typeContenu;
    private String lienOuText;
    private byte[] fichierLocal;
    private String typeFichier;
    private LocalDateTime datePublication;
    private LocalDateTime dateDerniereModification;
    private Long coursId; // ID du cours associé
}