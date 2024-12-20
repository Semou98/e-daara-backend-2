package com.sousgroupe2.e_daara.service.contenu;

import com.sousgroupe2.e_daara.DTO.ContenuDTO;
import com.sousgroupe2.e_daara.entities.cours.Contenu;
import com.sousgroupe2.e_daara.entities.cours.Cours;
import com.sousgroupe2.e_daara.mapper.ContenuMapper;
import com.sousgroupe2.e_daara.repository.cours.ContenuRepository;
import com.sousgroupe2.e_daara.repository.cours.CoursRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContenuImpl implements IContenuService {

    private final ContenuRepository contenuRepository;
    private final CoursRepository coursRepository;
    private final ContenuMapper contenuMapper;

    @Override
    public ContenuDTO createContenu(ContenuDTO dto) {
        validateContenuDTO(dto);
        Cours cours = coursRepository.findById(dto.getCoursId())
                .orElseThrow(() -> new EntityNotFoundException("Cours not found with id: " + dto.getCoursId()));
        Contenu contenu = contenuMapper.toEntity(dto);
        cours.addContenu(contenu); // Utilise la méthode utilitaire de Cours
        return contenuMapper.toDTO(contenuRepository.save(contenu));
    }

    @Transactional
    @Override
    public ContenuDTO updateContenu(Long id, ContenuDTO dto) {
        validateContenuDTO(dto);
        Contenu existingContenu = contenuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contenu not found with id: " + id));
        // Gérer le changement de cours si nécessaire
        if (!existingContenu.getCours().getId().equals(dto.getCoursId())) {
            Cours newCours = coursRepository.findById(dto.getCoursId())
                    .orElseThrow(() -> new EntityNotFoundException("Cours not found with id: " + dto.getCoursId()));
            existingContenu.getCours().removeContenu(existingContenu);
            newCours.addContenu(existingContenu);
        }
        updateContenuFromDTO(existingContenu, dto);
        return contenuMapper.toDTO(contenuRepository.save(existingContenu));
    }

    @Transactional(readOnly = true)
    @Override
    public ContenuDTO getContenuById(Long id) {
        return contenuMapper.toDTO(contenuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contenu not found with id: " + id)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ContenuDTO> getAllContenu() {
        return contenuMapper.toDTOList(contenuRepository.findAll());
    }

    @Transactional
    @Override
    public void deleteContenu(Long id) {
        Contenu contenu = contenuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contenu not found with id: " + id));
        contenu.getCours().removeContenu(contenu); // Utilise la méthode utilitaire de Cours
        contenuRepository.delete(contenu);
    }

    @Transactional
    @Override
    public ContenuDTO uploadFile(Long id, MultipartFile file) {
        Contenu contenu = contenuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contenu not found with id: " + id));
        try {
            contenu.setFichierLocal(file.getBytes());
            contenu.setTypeFichier(file.getContentType());
            contenu.setDateDerniereModification(LocalDateTime.now());
            return contenuMapper.toDTO(contenuRepository.save(contenu));
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }

    private void validateContenuDTO(ContenuDTO dto) {
        if (dto.getTitre() == null || dto.getTitre().isEmpty()) {
            throw new IllegalArgumentException("Titre is required");
        }
        if (dto.getTypeContenu() == null) {
            throw new IllegalArgumentException("TypeContenu is required");
        }
        if (dto.getCoursId() == null) {
            throw new IllegalArgumentException("CoursId is required");
        }
    }

    private void updateContenuFromDTO(Contenu contenu, ContenuDTO dto) {
        contenu.setTitre(dto.getTitre());
        contenu.setDescription(dto.getDescription());
        contenu.setTypeContenu(dto.getTypeContenu());
        contenu.setLienOuText(dto.getLienOuText());
        contenu.setDateDerniereModification(LocalDateTime.now());
    }
}
