package com.sousgroupe2.e_daara.mapper;

import com.sousgroupe2.e_daara.DTO.ContenuDTO;
import com.sousgroupe2.e_daara.entities.cours.Contenu;
import com.sousgroupe2.e_daara.repository.cours.CoursRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ContenuMapper {

    private final CoursRepository coursRepository;

    public ContenuDTO toDTO(Contenu contenu) {
        if (contenu == null) return null;

        return ContenuDTO.builder()
                .id(contenu.getId())
                .titre(contenu.getTitre())
                .description(contenu.getDescription())
                .typeContenu(contenu.getTypeContenu())
                .lienOuText(contenu.getLienOuText())
                .typeFichier(contenu.getTypeFichier())
                .datePublication(contenu.getDatePublication())
                .dateDerniereModification(contenu.getDateDerniereModification())
                .coursId(contenu.getCours() != null ? contenu.getCours().getId() : null)
                .build();
    }

    public Contenu toEntity(ContenuDTO dto) {
        if (dto == null) return null;

        Contenu contenu = Contenu.builder()
                .id(dto.getId())
                .titre(dto.getTitre())
                .description(dto.getDescription())
                .typeContenu(dto.getTypeContenu())
                .lienOuText(dto.getLienOuText())
                .typeFichier(dto.getTypeFichier())
                .datePublication(dto.getDatePublication())
                .dateDerniereModification(dto.getDateDerniereModification())
                .fichierLocal(dto.getFichierLocal())
                .build();

        if (dto.getCoursId() != null) {
            coursRepository.findById(dto.getCoursId())
                    .ifPresent(contenu::setCours);
        }

        return contenu;
    }

    public List<ContenuDTO> toDTOList(List<Contenu> contenus) {
        return contenus.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}