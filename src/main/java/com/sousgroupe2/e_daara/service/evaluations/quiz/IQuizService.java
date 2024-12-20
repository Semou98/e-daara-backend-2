package com.sousgroupe2.e_daara.service.evaluations.quiz;

import com.sousgroupe2.e_daara.DTO.quiz.QuestionDTO;
import com.sousgroupe2.e_daara.DTO.quiz.QuizDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IQuizService {


    @Transactional
    QuizDTO createQuiz(QuizDTO quizDTO);

    QuizDTO getQuizById(Long id);

    Page<QuizDTO> getAllQuizzes(Pageable pageable);

    @Transactional
    QuizDTO updateQuiz(Long id, QuizDTO quizDTO);

    @Transactional
    void deleteQuiz(Long id);

    @Transactional
    QuizDTO addQuestionToQuiz(Long quizId, QuestionDTO questionDTO);

    @Transactional
    void removeQuestionFromQuiz(Long quizId, Long questionId);
}
