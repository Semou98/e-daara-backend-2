package com.sousgroupe2.e_daara.exceptions;

public class QuizNotFoundException extends RuntimeException {
    public QuizNotFoundException(Long id) {
        super("Quiz non trouvé avec l'id: " + id);
    }
}