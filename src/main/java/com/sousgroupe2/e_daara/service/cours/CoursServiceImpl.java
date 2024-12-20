package com.sousgroupe2.e_daara.service.cours;

import com.sousgroupe2.e_daara.DTO.CoursDTO;
import com.sousgroupe2.e_daara.entities.cours.Contenu;
import com.sousgroupe2.e_daara.entities.cours.Cours;
import com.sousgroupe2.e_daara.entities.evaluations.Projet;
import com.sousgroupe2.e_daara.entities.evaluations.Quiz;
import com.sousgroupe2.e_daara.entities.parcours.Parcours;
import com.sousgroupe2.e_daara.mapper.CoursMapper;
import com.sousgroupe2.e_daara.repository.cours.ContenuRepository;
import com.sousgroupe2.e_daara.repository.cours.CoursRepository;
import com.sousgroupe2.e_daara.repository.evaluations.ProjetRepository;
import com.sousgroupe2.e_daara.repository.evaluations.QuizRepository;
import com.sousgroupe2.e_daara.repository.parcours.ParcoursRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoursServiceImpl implements ICoursService {

    private final CoursRepository coursRepository;
    private final ContenuRepository contenuRepository;
    private final QuizRepository quizRepository;
    private final ParcoursRepository parcoursRepository;
    private final ProjetRepository projetRepository;
    private final CoursMapper coursMapper;

    @Override
    public CoursDTO createCours(CoursDTO coursDTO) {
        validateCourseCreation(coursDTO);
        Cours cours = coursMapper.toEntity(coursDTO);
        // Validation et liaison des relations
        validateAndLinkRelations(cours, coursDTO);
        return coursMapper.toDTO(coursRepository.save(cours));
    }

    @Override
    public CoursDTO updateCours(Long id, CoursDTO coursDTO) {
        Cours existingCours = coursRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cours non trouvé avec l'id : " + id));
        // Mise à jour des propriétés de base
        existingCours.setTitre(coursDTO.getTitre());
        existingCours.setDescription(coursDTO.getDescription());
        existingCours.setIsCompleted(coursDTO.getIsCompleted());
        // Réinitialisation et validation des relations
        clearAndValidateRelations(existingCours, coursDTO);
        return coursMapper.toDTO(coursRepository.save(existingCours));
    }

    private void validateCourseCreation(CoursDTO coursDTO) {
        // Validation personnalisée si nécessaire
    }

    private void validateAndLinkRelations(Cours cours, CoursDTO coursDTO) {
        // Validation des contenus
        if (coursDTO.getContenusIds() != null) {
            List<Contenu> contenus = contenuRepository.findAllById(coursDTO.getContenusIds());
            validateEntityList(contenus, coursDTO.getContenusIds(), "Contenu");
            contenus.forEach(cours::addContenu);
        }
        // Méthodes similaires pour projets, quiz, parcours
    }

    private void clearAndValidateRelations(Cours cours, CoursDTO coursDTO) {
        // Effacer les relations existantes
        cours.getContenus().clear();
        cours.getProjets().clear();
        cours.getQuiz().clear();
        cours.getParcours().clear();
        // Revalider et réassocier
        validateAndLinkRelations(cours, coursDTO);
    }

    private void validateEntityList(List<?> entities, List<Long> ids, String entityName) {
        if (entities.size() != ids.size()) {
            throw new EntityNotFoundException(entityName + "s non trouvés");
        }
    }

    @Override
    public List<CoursDTO> getAllCours() {
        return coursRepository.findAll().stream()
                .map(coursMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CoursDTO getCoursById(Long id) {
        Cours cours = coursRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cours non trouvé avec l'id : " + id));
        return coursMapper.toDTO(cours);
    }

    @Override
    public void deleteCours(Long id) {
        Cours cours = coursRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cours non trouvé avec l'id : " + id));
        // Dissocier les relations avant suppression
        cours.getParcours().forEach(parcours -> parcours.getCours().remove(cours));
        coursRepository.delete(cours);
    }
}