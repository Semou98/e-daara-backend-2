package com.sousgroupe2.e_daara.repository.evaluations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sousgroupe2.e_daara.entities.evaluations.Quiz;

public interface QuizRepository extends JpaRepository<Quiz, Long>{

    Page<Quiz> findByCours_Id(Long coursId, Pageable pageable);
}
