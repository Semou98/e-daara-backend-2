package com.sousgroupe2.e_daara.mapper;

import com.sousgroupe2.e_daara.DTO.CoursDTO;
import com.sousgroupe2.e_daara.entities.cours.Contenu;
import com.sousgroupe2.e_daara.entities.cours.Cours;
import com.sousgroupe2.e_daara.entities.evaluations.Projet;
import com.sousgroupe2.e_daara.entities.evaluations.Quiz;
import com.sousgroupe2.e_daara.entities.parcours.Parcours;
import com.sousgroupe2.e_daara.repository.cours.ContenuRepository;
import com.sousgroupe2.e_daara.repository.evaluations.ProjetRepository;
import com.sousgroupe2.e_daara.repository.evaluations.QuizRepository;
import com.sousgroupe2.e_daara.repository.parcours.ParcoursRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CoursMapper {

    private final ContenuRepository contenuRepository;
    private final ProjetRepository projetRepository;
    private final QuizRepository quizRepository;
    private final ParcoursRepository parcoursRepository;

    public CoursMapper(ContenuRepository contenuRepository,
                       ProjetRepository projetRepository,
                       QuizRepository quizRepository,
                       ParcoursRepository parcoursRepository) {
        this.contenuRepository = contenuRepository;
        this.projetRepository = projetRepository;
        this.quizRepository = quizRepository;
        this.parcoursRepository = parcoursRepository;
    }

    public CoursDTO toDTO(Cours cours) {
        if (cours == null) {
            return null;
        }

        return CoursDTO.builder()
                .id(cours.getId())
                .titre(cours.getTitre())
                .description(cours.getDescription())
                .isCompleted(cours.getIsCompleted())
                .contenusIds(mapContenusToIds(cours.getContenus()))
                .projetsIds(mapProjetsToIds(cours.getProjets()))
                .quizIds(mapQuizToIds(cours.getQuiz()))
                .parcoursIds(mapParcoursToIds(cours.getParcours()))
                .build();
    }

    public Cours toEntity(CoursDTO dto) {
        if (dto == null) {
            return null;
        }

        Cours cours = Cours.builder()
                .id(dto.getId())
                .titre(dto.getTitre())
                .description(dto.getDescription())
                .isCompleted(dto.getIsCompleted())
                .build();

        // Set contenus if contenusIds are present
        if (dto.getContenusIds() != null && !dto.getContenusIds().isEmpty()) {
            List<Contenu> contenus = dto.getContenusIds().stream()
                    .map(contenuRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            cours.setContenus(contenus);
        }

        // Set projets if projetsIds are present
        if (dto.getProjetsIds() != null && !dto.getProjetsIds().isEmpty()) {
            List<Projet> projets = dto.getProjetsIds().stream()
                    .map(projetRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            cours.setProjets(projets);
        }

        // Set quiz if quizIds are present
        if (dto.getQuizIds() != null && !dto.getQuizIds().isEmpty()) {
            List<Quiz> quiz = dto.getQuizIds().stream()
                    .map(quizRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            cours.setQuiz(quiz);
        }

        // Set parcours if parcoursIds are present
        if (dto.getParcoursIds() != null && !dto.getParcoursIds().isEmpty()) {
            List<Parcours> parcours = dto.getParcoursIds().stream()
                    .map(parcoursRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            cours.setParcours(parcours);
        }

        return cours;
    }

    private List<Long> mapContenusToIds(List<Contenu> contenus) {
        return contenus == null ? new ArrayList<>() :
                contenus.stream()
                        .map(Contenu::getId)
                        .collect(Collectors.toList());
    }

    private List<Long> mapProjetsToIds(List<Projet> projets) {
        return projets == null ? new ArrayList<>() :
                projets.stream()
                        .map(Projet::getId)
                        .collect(Collectors.toList());
    }

    private List<Long> mapQuizToIds(List<Quiz> quiz) {
        return quiz == null ? new ArrayList<>() :
                quiz.stream()
                        .map(Quiz::getId)
                        .collect(Collectors.toList());
    }

    private List<Long> mapParcoursToIds(List<Parcours> parcours) {
        return parcours == null ? new ArrayList<>() :
                parcours.stream()
                        .map(Parcours::getId)
                        .collect(Collectors.toList());
    }

    public List<CoursDTO> toDTOList(List<Cours> cours) {
        return cours == null ? new ArrayList<>() :
                cours.stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList());
    }

    public List<Cours> toEntityList(List<CoursDTO> dtos) {
        return dtos == null ? new ArrayList<>() :
                dtos.stream()
                        .map(this::toEntity)
                        .collect(Collectors.toList());
    }
}