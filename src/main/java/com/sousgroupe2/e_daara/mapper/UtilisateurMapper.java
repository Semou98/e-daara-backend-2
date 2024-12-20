package com.sousgroupe2.e_daara.mapper;

import com.sousgroupe2.e_daara.DTO.UtilisateurDTO;
import com.sousgroupe2.e_daara.entities.parcours.Parcours;
import com.sousgroupe2.e_daara.entities.utilisateur.Utilisateur;
import com.sousgroupe2.e_daara.enums.ERole;
import com.sousgroupe2.e_daara.enums.EStatut;
import com.sousgroupe2.e_daara.repository.parcours.ParcoursRepository;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class UtilisateurMapper {

    private final ParcoursRepository parcoursRepository;

    public UtilisateurMapper(ParcoursRepository parcoursRepository) {
        this.parcoursRepository = parcoursRepository;
    }

    public UtilisateurDTO toDTO(Utilisateur utilisateur) {
        if (utilisateur == null) {
            return null;
        }

        return UtilisateurDTO.builder()
                .id(utilisateur.getId())
                .prenom(utilisateur.getPrenom())
                .nom(utilisateur.getNom())
                .sexe(utilisateur.getSexe())
                .email(utilisateur.getEmail())
                .telephone(utilisateur.getTelephone())
                .roles(utilisateur.getRoles())
                .statut(utilisateur.getStatut())
                .image(utilisateur.getImage())
                .photo(utilisateur.getPhoto())
                .typePhoto(utilisateur.getTypePhoto())
                .dateNaissance(utilisateur.getDateNaissance())
                .nationalite(utilisateur.getNationalite())
                .paysResidence(utilisateur.getPaysResidence())
                .dateCreation(utilisateur.getDateCreation())
                .dateDerniereModification(utilisateur.getDateDerniereModification())
                .parcoursIds(mapParcoursToIds(utilisateur.getParcours()))
                .build();
    }

    public Utilisateur toEntity(UtilisateurDTO dto) {
        if (dto == null) {
            return null;
        }

        Utilisateur utilisateur = Utilisateur.builder()
                .id(dto.getId())
                .prenom(dto.getPrenom())
                .nom(dto.getNom())
                .sexe(dto.getSexe())
                .email(dto.getEmail())
                .password(dto.getPassword()) // Note: gérer avec précaution
                .telephone(dto.getTelephone())
                .statut(dto.getStatut() != null ? dto.getStatut() : EStatut.INACTIF)
                .image(dto.getImage())
                .photo(dto.getPhoto())
                .typePhoto(dto.getTypePhoto())
                .dateNaissance(dto.getDateNaissance())
                .nationalite(dto.getNationalite())
                .paysResidence(dto.getPaysResidence())
                .dateCreation(dto.getDateCreation())
                .dateDerniereModification(dto.getDateDerniereModification())
                .build();

        // Définir les rôles
        if (dto.getRoles() != null && !dto.getRoles().isEmpty()) {
            utilisateur.setRoles(dto.getRoles());
        } else {
            utilisateur.setRoles(Set.of(ERole.ROLE_APPRENANT));
        }

        // Gérer les parcours
        if (dto.getParcoursIds() != null && !dto.getParcoursIds().isEmpty()) {
            List<Parcours> parcours = dto.getParcoursIds().stream()
                    .map(parcoursRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
            parcours.forEach(utilisateur::addParcours);
        }

        return utilisateur;
    }

    private List<Long> mapParcoursToIds(List<Parcours> parcours) {
        return parcours == null ? new ArrayList<>() :
                parcours.stream()
                        .map(Parcours::getId)
                        .collect(Collectors.toList());
    }

    public List<UtilisateurDTO> toDTOList(List<Utilisateur> utilisateurs) {
        return utilisateurs == null ? new ArrayList<>() :
                utilisateurs.stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList());
    }

    public List<Utilisateur> toEntityList(List<UtilisateurDTO> dtos) {
        return dtos == null ? new ArrayList<>() :
                dtos.stream()
                        .map(this::toEntity)
                        .collect(Collectors.toList());
    }
}