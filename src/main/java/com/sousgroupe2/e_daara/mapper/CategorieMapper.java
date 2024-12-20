package com.sousgroupe2.e_daara.mapper;

import com.sousgroupe2.e_daara.DTO.CategorieDTO;
import com.sousgroupe2.e_daara.entities.parcours.Categorie;
import com.sousgroupe2.e_daara.entities.parcours.Parcours;
import com.sousgroupe2.e_daara.repository.parcours.ParcoursRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CategorieMapper {

    private final ParcoursRepository parcoursRepository;

    public CategorieDTO toDTO(Categorie categorie) {
        if (categorie == null) {
            return null;
        }

        return CategorieDTO.builder()
                .id(categorie.getId())
                .nom(categorie.getNom())
                .description(categorie.getDescription())
                .image(categorie.getImage())
                .parcoursIds(mapParcoursToIds(categorie.getParcours()))
                .build();
    }

    public Categorie toEntity(CategorieDTO dto) {
        if (dto == null) {
            return null;
        }

        Categorie categorie = Categorie.builder()
                .id(dto.getId())
                .nom(dto.getNom())
                .description(dto.getDescription())
                .image(dto.getImage())
                .build();

        if (dto.getParcoursIds() != null && !dto.getParcoursIds().isEmpty()) {
            List<Parcours> parcours = dto.getParcoursIds().stream()
                    .map(id -> parcoursRepository.findById(id)
                            .orElseThrow(() -> new EntityNotFoundException("Parcours not found: " + id)))
                    .toList();
            parcours.forEach(categorie::addParcours);
        }

        return categorie;
    }

    private List<Long> mapParcoursToIds(List<Parcours> parcours) {
        return parcours == null ? new ArrayList<>() :
                parcours.stream()
                        .map(Parcours::getId)
                        .collect(Collectors.toList());
    }

    public List<CategorieDTO> toDTOList(List<Categorie> categories) {
        return categories == null ? new ArrayList<>() :
                categories.stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList());
    }
}