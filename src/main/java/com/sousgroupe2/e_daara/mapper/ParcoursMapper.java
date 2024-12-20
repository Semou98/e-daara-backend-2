package com.sousgroupe2.e_daara.mapper;

import com.sousgroupe2.e_daara.DTO.ParcoursDTO;
import com.sousgroupe2.e_daara.entities.cours.Cours;
import com.sousgroupe2.e_daara.entities.parcours.Parcours;
import com.sousgroupe2.e_daara.entities.utilisateur.Utilisateur;
import com.sousgroupe2.e_daara.repository.cours.CoursRepository;
import com.sousgroupe2.e_daara.repository.parcours.CategorieRepository;
import com.sousgroupe2.e_daara.repository.utilisateur.UtilisateurRepository;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ParcoursMapper {

    private final CategorieRepository categorieRepository;
    private final CoursRepository coursRepository;
    private final UtilisateurRepository utilisateurRepository;

    public ParcoursMapper(CategorieRepository categorieRepository,
                          CoursRepository coursRepository,
                          UtilisateurRepository utilisateurRepository) {
        this.categorieRepository = categorieRepository;
        this.coursRepository = coursRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public ParcoursDTO toDTO(Parcours parcours) {
        if (parcours == null) {
            return null;
        }

        return ParcoursDTO.builder()
                .id(parcours.getId())
                .nom(parcours.getNom())
                .description(parcours.getDescription())
                .difficulte(parcours.getDifficulte())
                .prerequis(parcours.getPrerequis())
                .duree(parcours.getDureeString())
                .dateDebut(parcours.getDateDebut())
                .dateFin(parcours.getDateFin())
                .progression(parcours.getProgression())
                .image(parcours.getImage())
                .imageType(parcours.getImageType())
                .categorieId(parcours.getCategorie() != null ? parcours.getCategorie().getId() : null)
                .coursIds(mapCoursToIds(parcours.getCours()))
                .utilisateurIds(mapUtilisateurToIds(parcours.getUtilisateurs()))
                .build();
    }

    public Parcours toEntity(ParcoursDTO dto) {
        if (dto == null) {
            return null;
        }

        Parcours parcours = Parcours.builder()
                .id(dto.getId())
                .nom(dto.getNom())
                .description(dto.getDescription())
                .difficulte(dto.getDifficulte())
                .prerequis(dto.getPrerequis())
                .dureeString(dto.getDuree())
                .dateDebut(dto.getDateDebut())
                .dateFin(dto.getDateFin())
                .progression(dto.getProgression())
                .image(dto.getImage())
                .imageType(dto.getImageType())
                .build();

        // Set categorie if categorieId is present
        if (dto.getCategorieId() != null) {
            categorieRepository.findById(dto.getCategorieId())
                    .ifPresent(parcours::setCategorie);
        }

        // Set cours if coursIds are present
        if (dto.getCoursIds() != null && !dto.getCoursIds().isEmpty()) {
            List<Cours> cours = dto.getCoursIds().stream()
                    .map(id -> coursRepository.findById(id))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            parcours.setCours(cours);
        }

        // Set utilisateurs if utilisateurIds are present
        if (dto.getUtilisateurIds() != null && !dto.getUtilisateurIds().isEmpty()) {
            List<Utilisateur> utilisateurs = dto.getUtilisateurIds().stream()
                    .map(id -> utilisateurRepository.findById(id))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            parcours.setUtilisateurs(utilisateurs);
        }

        return parcours;
    }

    private List<Long> mapCoursToIds(List<Cours> cours) {
        return cours == null ? new ArrayList<>() :
                cours.stream()
                        .map(Cours::getId)
                        .collect(Collectors.toList());
    }

    private List<Long> mapUtilisateurToIds(List<Utilisateur> utilisateurs) {
        return utilisateurs == null ? new ArrayList<>() :
                utilisateurs.stream()
                        .map(Utilisateur::getId)
                        .collect(Collectors.toList());
    }

    public List<ParcoursDTO> toDTOList(List<Parcours> parcours) {
        return parcours == null ? new ArrayList<>() :
                parcours.stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList());
    }

    public List<Parcours> toEntityList(List<ParcoursDTO> dtos) {
        return dtos == null ? new ArrayList<>() :
                dtos.stream()
                        .map(this::toEntity)
                        .collect(Collectors.toList());
    }
}