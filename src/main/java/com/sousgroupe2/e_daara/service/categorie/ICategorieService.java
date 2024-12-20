package com.sousgroupe2.e_daara.service.categorie;

import com.sousgroupe2.e_daara.DTO.CategorieDTO;
import com.sousgroupe2.e_daara.DTO.ParcoursDTO;
import com.sousgroupe2.e_daara.entities.parcours.Categorie;
import com.sousgroupe2.e_daara.entities.parcours.Parcours;
import jakarta.transaction.Transactional;

import java.util.List;

public interface ICategorieService {

    CategorieDTO createCategorie(CategorieDTO categorieDTO);

    CategorieDTO getCategorieById(Long id);

    List<CategorieDTO> getAllCategories();

    CategorieDTO updateCategorie(Long id, CategorieDTO categorieDTO);

    void deleteCategorie(Long id);

    List<ParcoursDTO> getParcoursByCategorie(Long categorieId);
}
