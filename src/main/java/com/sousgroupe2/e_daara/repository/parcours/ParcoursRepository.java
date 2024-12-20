package com.sousgroupe2.e_daara.repository.parcours;

import com.sousgroupe2.e_daara.entities.parcours.Categorie;
import com.sousgroupe2.e_daara.entities.parcours.Parcours;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParcoursRepository extends JpaRepository<Parcours, Long> {
    List<Parcours> findByCategorie(Categorie categorie);
}
