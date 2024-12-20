package com.sousgroupe2.e_daara.repository.parcours;

import com.sousgroupe2.e_daara.entities.parcours.Categorie;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategorieRepository extends JpaRepository<Categorie, Long>{
    boolean existsByNomIgnoreCase(@NotNull String nom);
}
