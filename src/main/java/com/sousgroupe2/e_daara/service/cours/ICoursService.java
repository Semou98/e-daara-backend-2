package com.sousgroupe2.e_daara.service.cours;

import com.sousgroupe2.e_daara.DTO.CoursDTO;
import java.util.List;

public interface ICoursService {

    CoursDTO createCours(CoursDTO coursDTO);

    CoursDTO updateCours(Long id, CoursDTO coursDTO);

    List<CoursDTO> getAllCours();

    CoursDTO getCoursById(Long id);

    void deleteCours(Long id);
}