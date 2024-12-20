package com.sousgroupe2.e_daara.mapper;

import com.sousgroupe2.e_daara.DTO.quiz.QuestionDTO;
import com.sousgroupe2.e_daara.DTO.quiz.QuizDTO;
import com.sousgroupe2.e_daara.DTO.quiz.ReponseDTO;
import com.sousgroupe2.e_daara.entities.cours.Cours;
import com.sousgroupe2.e_daara.entities.evaluations.Question;
import com.sousgroupe2.e_daara.entities.evaluations.Quiz;
import com.sousgroupe2.e_daara.entities.evaluations.Reponse;
import com.sousgroupe2.e_daara.repository.cours.CoursRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class QuizMapper {

    private final CoursRepository coursRepository;

    public Quiz toEntity(QuizDTO dto) {
        if (dto == null) return null;

        Quiz quiz = new Quiz();
        quiz.setId(dto.getId());
        quiz.setTitre(dto.getTitre());
        quiz.setDescription(dto.getDescription());
        quiz.setDifficulte(dto.getDifficulte());
        quiz.setDateOuverture(dto.getDateOuverture());
        quiz.setDateFermeture(dto.getDateFermeture());
        quiz.setDelai(dto.getDelai());
        quiz.setNombreQuestion(dto.getNombreQuestion());
        quiz.setDuree(dto.getDuree() != null ?
                Duration.ofMinutes(Long.parseLong(dto.getDuree())) : null);
        quiz.setNote(dto.getNote());

        if (dto.getCoursId() != null) {
            Cours cours = coursRepository.findById(dto.getCoursId())
                    .orElseThrow(() -> new EntityNotFoundException("Cours not found: " + dto.getCoursId()));
            quiz.setCours(cours);
        }

        if (dto.getQuestions() != null) {
            dto.getQuestions().forEach(questionDTO -> {
                Question question = questionToEntity(questionDTO);
                quiz.addQuestion(question);
            });
        }

        return quiz;
    }

    public QuizDTO toDto(Quiz quiz) {
        if (quiz == null) return null;

        return QuizDTO.builder()
                .id(quiz.getId())
                .titre(quiz.getTitre())
                .description(quiz.getDescription())
                .difficulte(quiz.getDifficulte())
                .dateOuverture(quiz.getDateOuverture())
                .dateFermeture(quiz.getDateFermeture())
                .delai(quiz.getDelai())
                .nombreQuestion(quiz.getNombreQuestion())
                .duree(quiz.getDuree() != null ? String.valueOf(quiz.getDuree().toMinutes()) : null)
                .note(quiz.getNote())
                .coursId(quiz.getCours() != null ? quiz.getCours().getId() : null)
                .questions(quiz.getQuestions().stream()
                        .map(this::questionToDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private QuestionDTO questionToDto(Question question) {
        if (question == null) return null;

        return QuestionDTO.builder()
                .id(question.getId())
                .type(question.getType())
                .points(question.getPoints())
                .textQuestion(question.getTextQuestion())
                .reponses(question.getReponses().stream()
                        .map(this::reponseToDto)
                        .collect(Collectors.toList()))
                .build();
    }

    public Question questionToEntity(QuestionDTO dto) {
        if (dto == null) return null;

        Question question = new Question();
        question.setId(dto.getId());
        question.setType(dto.getType());
        question.setPoints(dto.getPoints());
        question.setTextQuestion(dto.getTextQuestion());

        if (dto.getReponses() != null) {
            dto.getReponses().forEach(reponseDTO -> {
                Reponse reponse = reponseToEntity(reponseDTO);
                question.addReponse(reponse);
            });
        }

        return question;
    }

    private ReponseDTO reponseToDto(Reponse reponse) {
        if (reponse == null) return null;
        return ReponseDTO.builder()
                .id(reponse.getId())
                .optionReponse(reponse.getOptionReponse())
                .estCorrecte(reponse.getEstCorrecte())
                .build();
    }

    private Reponse reponseToEntity(ReponseDTO dto) {
        if (dto == null) return null;
        return Reponse.builder()
                .id(dto.getId())
                .optionReponse(dto.getOptionReponse())
                .estCorrecte(dto.getEstCorrecte())
                .build();
    }
}