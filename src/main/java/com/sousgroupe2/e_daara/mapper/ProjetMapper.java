package com.sousgroupe2.e_daara.mapper;

import com.sousgroupe2.e_daara.DTO.ProjetDTO;
import com.sousgroupe2.e_daara.entities.cours.Cours;
import com.sousgroupe2.e_daara.entities.evaluations.Projet;
import com.sousgroupe2.e_daara.repository.cours.CoursRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProjetMapper {
    private final CoursRepository coursRepository;

    public ProjetDTO toDTO(Projet projet) {
        if (projet == null) {
            return null;
        }

        return ProjetDTO.builder()
                .id(projet.getId())
                .titre(projet.getTitre())
                .description(projet.getDescription())
                .difficulte(projet.getDifficulte())
                .dateOuverture(projet.getDateOuverture())
                .dateFermeture(projet.getDateFermeture())
                .delai(projet.getDelai())
                .urlFichier(projet.getUrlFichier())
                .note(projet.getNote())
                .coursId(projet.getCours() != null ? projet.getCours().getId() : null)
                .build();
    }

    public Projet toEntity(ProjetDTO dto) {
        if (dto == null) {
            return null;
        }

        Projet projet = Projet.builder()
                .id(dto.getId())
                .titre(dto.getTitre())
                .description(dto.getDescription())
                .difficulte(dto.getDifficulte())
                .dateOuverture(dto.getDateOuverture())
                .dateFermeture(dto.getDateFermeture())
                .delai(dto.getDelai())
                .urlFichier(dto.getUrlFichier())
                .note(dto.getNote())
                .build();

        if (dto.getCoursId() != null) {
            Cours cours = coursRepository.findById(dto.getCoursId())
                    .orElseThrow(() -> new EntityNotFoundException("Cours non trouvé avec l'id: " + dto.getCoursId()));
            projet.setCours(cours);
        }

        return projet;
    }

    public List<ProjetDTO> toDTOList(List<Projet> projets) {
        return projets == null ? new ArrayList<>() :
                projets.stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList());
    }
}
