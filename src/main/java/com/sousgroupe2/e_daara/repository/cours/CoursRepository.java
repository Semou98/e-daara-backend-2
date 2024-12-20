package com.sousgroupe2.e_daara.repository.cours;

import com.sousgroupe2.e_daara.entities.cours.Cours;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CoursRepository extends JpaRepository<Cours, Long> {
    List<Cours> findByParcoursId(Long parcoursId);
}
