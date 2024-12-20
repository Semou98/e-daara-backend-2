package com.sousgroupe2.e_daara.service.contenu;

import com.sousgroupe2.e_daara.DTO.ContenuDTO;
import com.sousgroupe2.e_daara.entities.cours.Contenu;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IContenuService {

    ContenuDTO createContenu(ContenuDTO dto);

    ContenuDTO updateContenu(Long id, ContenuDTO dto);

    ContenuDTO getContenuById(Long id);

    List<ContenuDTO> getAllContenu();

    void deleteContenu(Long id);

    ContenuDTO uploadFile(Long id, MultipartFile file);
}
