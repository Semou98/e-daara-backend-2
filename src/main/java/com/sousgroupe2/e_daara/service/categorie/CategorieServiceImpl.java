package com.sousgroupe2.e_daara.service.categorie;

import com.sousgroupe2.e_daara.DTO.CategorieDTO;
import com.sousgroupe2.e_daara.DTO.ParcoursDTO;
import com.sousgroupe2.e_daara.entities.parcours.Categorie;
import com.sousgroupe2.e_daara.entities.parcours.Parcours;
import com.sousgroupe2.e_daara.exceptions.DuplicateEntityException;
import com.sousgroupe2.e_daara.mapper.CategorieMapper;
import com.sousgroupe2.e_daara.mapper.ParcoursMapper;
import com.sousgroupe2.e_daara.repository.parcours.CategorieRepository;
import com.sousgroupe2.e_daara.repository.parcours.ParcoursRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategorieServiceImpl implements ICategorieService {

    private final CategorieRepository categorieRepository;
    private final ParcoursRepository parcoursRepository;
    private final CategorieMapper categorieMapper;
    private final ParcoursMapper parcoursMapper;

    @Transactional
    @Override
    public CategorieDTO createCategorie(CategorieDTO categorieDTO) {
        validateCategorieCreation(categorieDTO);
        Categorie categorie = categorieMapper.toEntity(categorieDTO);
        return categorieMapper.toDTO(categorieRepository.save(categorie));
    }

    @Override
    public CategorieDTO getCategorieById(Long id) {
        return categorieMapper.toDTO(findCategorieById(id));
    }

    @Override
    public List<CategorieDTO> getAllCategories() {
        return categorieMapper.toDTOList(categorieRepository.findAll());
    }

    @Transactional
    @Override
    public CategorieDTO updateCategorie(Long id, CategorieDTO categorieDTO) {
        Categorie existingCategorie = findCategorieById(id);
        validateCategorieUpdate(categorieDTO, existingCategorie);

        updateCategorieFields(existingCategorie, categorieDTO);
        return categorieMapper.toDTO(categorieRepository.save(existingCategorie));
    }

    @Transactional
    @Override
    public void deleteCategorie(Long id) {
        Categorie categorie = findCategorieById(id);
        categorieRepository.delete(categorie);
    }

    @Transactional
    @Override
    public List<ParcoursDTO> getParcoursByCategorie(Long categorieId) {
        Categorie categorie = findCategorieById(categorieId);
        return parcoursMapper.toDTOList(parcoursRepository.findByCategorie(categorie));
    }

    private Categorie findCategorieById(Long id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Categorie non trouvée avec l'id : " + id));
    }

    // METHODE UTILITAIRES

    private void validateCategorieCreation(CategorieDTO categorieDTO) {
        if (categorieRepository.existsByNomIgnoreCase(categorieDTO.getNom())) {
            throw new DuplicateEntityException("Une catégorie avec le nom '" +
                    categorieDTO.getNom() + "' existe déjà.");
        }
    }

    private void validateCategorieUpdate(CategorieDTO categorieDTO, Categorie existingCategorie) {
        if (!existingCategorie.getNom().equalsIgnoreCase(categorieDTO.getNom()) &&
                categorieRepository.existsByNomIgnoreCase(categorieDTO.getNom())) {
            throw new DuplicateEntityException("Une catégorie avec le nom '" +
                    categorieDTO.getNom() + "' existe déjà.");
        }
    }

    private void updateCategorieFields(Categorie categorie, CategorieDTO dto) {
        categorie.setNom(dto.getNom());
        categorie.setDescription(dto.getDescription());
        categorie.setImage(dto.getImage());

        if (dto.getParcoursIds() != null) {
            categorie.getParcours().clear();
            List<Parcours> parcours = parcoursRepository.findAllById(dto.getParcoursIds());
            parcours.forEach(categorie::addParcours);
        }
    }
}
