package com.sousgroupe2.e_daara.service.parcours;

import com.sousgroupe2.e_daara.DTO.ParcoursDTO;
import com.sousgroupe2.e_daara.entities.cours.Cours;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IParcoursService {

    ParcoursDTO createParcours(ParcoursDTO parcoursDTO);

    void uploadImage(Long id, MultipartFile imageFile);

    ParcoursDTO updateParcours(Long id, ParcoursDTO parcoursDTO);

    List<ParcoursDTO> getAllParcours();

    ParcoursDTO getParcoursById(Long id);

    void deleteParcours(Long id);

    void addUtilisateursToParcours(Long parcoursId, List<Long> utilisateurIds);

    void removeUtilisateursFromParcours(Long parcoursId, List<Long> utilisateurIds);
}
