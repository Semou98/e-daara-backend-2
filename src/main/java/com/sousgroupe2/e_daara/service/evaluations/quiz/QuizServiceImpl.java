package com.sousgroupe2.e_daara.service.evaluations.quiz;

import com.sousgroupe2.e_daara.DTO.quiz.QuizDTO;
import com.sousgroupe2.e_daara.DTO.quiz.ReponseDTO;
import com.sousgroupe2.e_daara.DTO.quiz.QuestionDTO;
import com.sousgroupe2.e_daara.entities.evaluations.Quiz;
import com.sousgroupe2.e_daara.entities.evaluations.Question;
import com.sousgroupe2.e_daara.exceptions.QuizNotFoundException;
import com.sousgroupe2.e_daara.exceptions.QuizValidationException;
import com.sousgroupe2.e_daara.mapper.QuizMapper;
import com.sousgroupe2.e_daara.repository.evaluations.QuizRepository;
import com.sousgroupe2.e_daara.repository.evaluations.QuestionRepository;
import com.sousgroupe2.e_daara.repository.cours.CoursRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor @Builder
public class QuizServiceImpl implements IQuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final CoursRepository coursRepository;
    private final QuizMapper quizMapper;

    @Transactional
    @Override
    public QuizDTO createQuiz(QuizDTO quizDTO) {
        validateQuizData(quizDTO);
        Quiz quiz = quizMapper.toEntity(quizDTO);
        return quizMapper.toDto(quizRepository.save(quiz));
    }

    @Override
    public QuizDTO getQuizById(Long id) {
        return quizRepository.findById(id)
                .map(quizMapper::toDto)
                .orElseThrow(() -> new QuizNotFoundException(id));
    }

    @Override
    public Page<QuizDTO> getAllQuizzes(Pageable pageable) {
        return quizRepository.findAll(pageable)
                .map(quizMapper::toDto);
    }

    @Transactional
    @Override
    public QuizDTO updateQuiz(Long id, QuizDTO quizDTO) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException(id));

        validateQuizData(quizDTO);
        quizDTO.setId(id);
        Quiz updatedQuiz = quizMapper.toEntity(quizDTO);
        return quizMapper.toDto(quizRepository.save(updatedQuiz));
    }

    @Transactional
    @Override
    public void deleteQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException(id));
        quizRepository.delete(quiz);
    }

    @Transactional
    @Override
    public QuizDTO addQuestionToQuiz(Long quizId, QuestionDTO questionDTO) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException(quizId));

        validateQuestionData(questionDTO);
        Question question = quizMapper.questionToEntity(questionDTO);
        quiz.addQuestion(question);

        return quizMapper.toDto(quizRepository.save(quiz));
    }

    @Transactional
    @Override
    public void removeQuestionFromQuiz(Long quizId, Long questionId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException(quizId));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question not found: " + questionId));

        if (!quiz.getQuestions().contains(question)) {
            throw new QuizValidationException("Cette question n'appartient pas à ce quiz");
        }

        quiz.removeQuestion(question);
        quizRepository.save(quiz);
    }

    private void validateQuizData(QuizDTO dto) {
        if (dto.getDateFermeture() != null &&
                dto.getDateOuverture() != null &&
                dto.getDateFermeture().isBefore(dto.getDateOuverture())) {
            throw new QuizValidationException("La date de fermeture doit être après la date d'ouverture");
        }

        if (dto.getCoursId() != null && !coursRepository.existsById(dto.getCoursId())) {
            throw new EntityNotFoundException("Cours not found: " + dto.getCoursId());
        }
    }

    private void validateQuestionData(QuestionDTO questionDTO) {
        if (questionDTO.getReponses() == null || questionDTO.getReponses().isEmpty()) {
            throw new QuizValidationException("Une question doit avoir au moins une réponse");
        }

        boolean hasCorrectAnswer = questionDTO.getReponses().stream()
                .anyMatch(ReponseDTO::getEstCorrecte);
        if (!hasCorrectAnswer) {
            throw new QuizValidationException("Une question doit avoir au moins une réponse correcte");
        }
    }
}