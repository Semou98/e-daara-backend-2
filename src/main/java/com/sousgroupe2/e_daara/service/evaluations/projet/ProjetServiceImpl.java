package com.sousgroupe2.e_daara.service.evaluations.projet;

import com.sousgroupe2.e_daara.entities.cours.Cours;
import com.sousgroupe2.e_daara.entities.evaluations.Projet;
import com.sousgroupe2.e_daara.mapper.ProjetMapper;
import com.sousgroupe2.e_daara.repository.evaluations.ProjetRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import com.sousgroupe2.e_daara.DTO.ProjetDTO;
import com.sousgroupe2.e_daara.repository.cours.CoursRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProjetServiceImpl implements IProjetService {

    private final ProjetRepository projetRepository;
    private final ProjetMapper projetMapper;
    private final CoursRepository coursRepository;

    @Override
    @Transactional
    public ProjetDTO createProjet(ProjetDTO projetDTO) {
        validateProjetCreation(projetDTO);
        Projet projet = projetMapper.toEntity(projetDTO);
        return projetMapper.toDTO(projetRepository.save(projet));
    }

    @Override
    @Transactional(readOnly = true)
    public ProjetDTO getProjetById(Long id) {
        return projetMapper.toDTO(findProjetById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjetDTO> getAllProjets() {
        return projetMapper.toDTOList(projetRepository.findAll());
    }

    @Override
    @Transactional
    public ProjetDTO updateProjet(Long id, ProjetDTO projetDTO) {
        Projet existingProjet = findProjetById(id);
        updateProjetFields(existingProjet, projetDTO);
        return projetMapper.toDTO(projetRepository.save(existingProjet));
    }

    @Override
    @Transactional
    public void deleteProjet(Long id) {
        Projet projet = findProjetById(id);
        if (projet.getCours() != null) {
            projet.setCours(null);
        }
        projetRepository.delete(projet);
    }

    private Projet findProjetById(Long id) {
        return projetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé avec l'id : " + id));
    }

    private void validateProjetCreation(ProjetDTO projetDTO) {
        validateDates(projetDTO);
        if (projetDTO.getCoursId() != null) {
            coursRepository.findById(projetDTO.getCoursId())
                    .orElseThrow(() -> new EntityNotFoundException("Cours non trouvé avec l'id : " + projetDTO.getCoursId()));
        }
    }

    private void validateDates(ProjetDTO projetDTO) {
        if (projetDTO.getDateOuverture() != null && projetDTO.getDateFermeture() != null
                && projetDTO.getDateOuverture().isAfter(projetDTO.getDateFermeture())) {
            throw new IllegalArgumentException("La date d'ouverture doit être antérieure à la date de fermeture");
        }
    }

    private void updateProjetFields(Projet projet, ProjetDTO projetDTO) {
        projet.setTitre(projetDTO.getTitre());
        projet.setDescription(projetDTO.getDescription());
        projet.setDifficulte(projetDTO.getDifficulte());
        projet.setDateOuverture(projetDTO.getDateOuverture());
        projet.setDateFermeture(projetDTO.getDateFermeture());
        projet.setDelai(projetDTO.getDelai());
        projet.setUrlFichier(projetDTO.getUrlFichier());
        projet.setNote(projetDTO.getNote());
        if (projetDTO.getCoursId() != null) {
            Cours cours = coursRepository.findById(projetDTO.getCoursId())
                    .orElseThrow(() -> new EntityNotFoundException("Cours non trouvé avec l'id : " + projetDTO.getCoursId()));
            projet.setCours(cours);
        } else {
            projet.setCours(null);
        }
    }
}