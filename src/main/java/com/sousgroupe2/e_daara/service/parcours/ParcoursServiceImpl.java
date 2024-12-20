package com.sousgroupe2.e_daara.service.parcours;

import com.sousgroupe2.e_daara.entities.parcours.Categorie;
import com.sousgroupe2.e_daara.entities.parcours.Parcours;
import com.sousgroupe2.e_daara.entities.utilisateur.Utilisateur;
import com.sousgroupe2.e_daara.entities.cours.Cours;
import com.sousgroupe2.e_daara.DTO.ParcoursDTO;
import com.sousgroupe2.e_daara.mapper.ParcoursMapper;
import com.sousgroupe2.e_daara.repository.cours.CoursRepository;
import com.sousgroupe2.e_daara.repository.parcours.ParcoursRepository;
import com.sousgroupe2.e_daara.repository.parcours.CategorieRepository;
import com.sousgroupe2.e_daara.repository.utilisateur.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ParcoursServiceImpl implements IParcoursService {

    private ParcoursRepository parcoursRepository;
    private CategorieRepository categorieRepository;
    private CoursRepository coursRepository;
    private UtilisateurRepository utilisateurRepository;
    private final ParcoursMapper parcoursMapper;

    @Override
    @Transactional
    public ParcoursDTO createParcours(@Valid ParcoursDTO parcoursDTO) {
        validateParcoursCreation(parcoursDTO);

        // Conversion de DTO à entité
        Parcours parcours = parcoursMapper.toEntity(parcoursDTO);

        // Gestion de la catégorie
        if (parcoursDTO.getCategorieId() != null) {
            parcours.setCategorie(findCategorieByIdOrThrow(parcoursDTO.getCategorieId()));
        }

        // Gestion des cours
        if (parcoursDTO.getCoursIds() != null && !parcoursDTO.getCoursIds().isEmpty()) {
            parcours.setCours(findAndValidateCours(parcoursDTO.getCoursIds()));
        }

        // Gestion des utilisateurs
        if (parcoursDTO.getUtilisateurIds() != null && !parcoursDTO.getUtilisateurIds().isEmpty()) {
            parcours.setUtilisateurs(findAndValidateUtilisateurs(parcoursDTO.getUtilisateurIds()));
        }

        return parcoursMapper.toDTO(parcoursRepository.save(parcours));
    }

    // Méthode utilitaire pour trouver et valider les utilisateurs
    private List<Utilisateur> findAndValidateUtilisateurs(List<Long> ids) {
        List<Utilisateur> utilisateurs = utilisateurRepository.findAllById(ids);
        validateUtilisateursExist(ids, utilisateurs);
        return utilisateurs;
    }

    // Méthode de validation des utilisateurs
    private void validateUtilisateursExist(List<Long> ids, List<Utilisateur> utilisateurs) {
        Set<Long> foundIds = utilisateurs.stream().map(Utilisateur::getId).collect(Collectors.toSet());
        List<Long> missingIds = ids.stream().filter(id -> !foundIds.contains(id)).collect(Collectors.toList());
        if (!missingIds.isEmpty()) {
            throw new EntityNotFoundException("Utilisateurs non trouvés : " + missingIds);
        }
    }

    @Override
    @Transactional
    public ParcoursDTO updateParcours(Long id, @Valid ParcoursDTO parcoursDTO) {
        Parcours existingParcours = findParcoursByIdOrThrow(id);

        validateParcoursCreation(parcoursDTO);

        // Mise à jour des propriétés simples
        existingParcours.setNom(parcoursDTO.getNom());
        existingParcours.setDescription(parcoursDTO.getDescription());
        existingParcours.setDifficulte(parcoursDTO.getDifficulte());
        existingParcours.setPrerequis(parcoursDTO.getPrerequis());
        existingParcours.setDureeString(parcoursDTO.getDuree());
        existingParcours.setDateDebut(parcoursDTO.getDateDebut());
        existingParcours.setDateFin(parcoursDTO.getDateFin());
        existingParcours.setProgression(parcoursDTO.getProgression());

        // Mise à jour de la catégorie
        if (parcoursDTO.getCategorieId() != null) {
            existingParcours.setCategorie(findCategorieByIdOrThrow(parcoursDTO.getCategorieId()));
        }

        // Mise à jour des cours
        if (parcoursDTO.getCoursIds() != null) {
            existingParcours.clearCours();
            if (!parcoursDTO.getCoursIds().isEmpty()) {
                existingParcours.setCours(findAndValidateCours(parcoursDTO.getCoursIds()));
            }
        }

        return parcoursMapper.toDTO(parcoursRepository.save(existingParcours));
    }

    @Override
    public void uploadImage(Long id, MultipartFile imageFile) {
        Parcours parcours = findParcoursByIdOrThrow(id);

        if (imageFile.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        try {
            parcours.setImage(imageFile.getBytes());
            parcours.setImageType(imageFile.getContentType());
            parcoursRepository.save(parcours);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'upload de l'image", e);
        }
    }

    @Override
    public void deleteParcours(Long id) {
        Parcours parcours = findParcoursByIdOrThrow(id);
        parcours.clearCours();
        parcours.getUtilisateurs().clear();
        parcoursRepository.delete(parcours);
    }

    @Override
    public List<ParcoursDTO> getAllParcours() {
        return parcoursRepository.findAll().stream()
                .map(parcoursMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ParcoursDTO getParcoursById(Long id) {
        return parcoursMapper.toDTO(findParcoursByIdOrThrow(id));
    }

    @Override
    public void addUtilisateursToParcours(Long parcoursId, List<Long> utilisateurIds) {
        Parcours parcours = findParcoursByIdOrThrow(parcoursId);
        List<Utilisateur> utilisateurs = utilisateurRepository.findAllById(utilisateurIds);

        utilisateurs.forEach(utilisateur -> {
            if (!parcours.getUtilisateurs().contains(utilisateur)) {
                parcours.getUtilisateurs().add(utilisateur);
                utilisateur.getParcours().add(parcours);
            }
        });

        parcoursRepository.save(parcours);
    }

    @Override
    public void removeUtilisateursFromParcours(Long parcoursId, List<Long> utilisateurIds) {
        Parcours parcours = findParcoursByIdOrThrow(parcoursId);
        List<Utilisateur> utilisateurs = utilisateurRepository.findAllById(utilisateurIds);

        utilisateurs.forEach(utilisateur -> {
            if (parcours.getUtilisateurs().remove(utilisateur)) {
                utilisateur.getParcours().remove(parcours);
            }
        });

        parcoursRepository.save(parcours);
    }

    // Méthodes utilitaires privées
    private Parcours findParcoursByIdOrThrow(Long id) {
        return parcoursRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Parcours non trouvé avec l'id : " + id));
    }

    private Categorie findCategorieByIdOrThrow(Long id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Catégorie non trouvée avec l'id : " + id));
    }

    private List<Cours> findAndValidateCours(List<Long> ids) {
        List<Cours> cours = coursRepository.findAllById(ids);
        validateCoursExist(ids, cours);
        return cours;
    }

    private void validateParcoursCreation(ParcoursDTO dto) {
        if (dto.getDateDebut() != null && dto.getDateFin() != null
                && dto.getDateDebut().isAfter(dto.getDateFin())) {
            throw new IllegalArgumentException("La date de début doit être avant la date de fin");
        }
    }

    private void validateCoursExist(List<Long> ids, List<Cours> cours) {
        Set<Long> foundIds = cours.stream().map(Cours::getId).collect(Collectors.toSet());
        List<Long> missingIds = ids.stream().filter(id -> !foundIds.contains(id)).collect(Collectors.toList());
        if (!missingIds.isEmpty()) {
            throw new EntityNotFoundException("Cours non trouvés : " + missingIds);
        }
    }
}