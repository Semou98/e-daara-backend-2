package com.sousgroupe2.e_daara.service.evaluations.projet;

import com.sousgroupe2.e_daara.DTO.ProjetDTO;

import java.util.List;

public interface IProjetService {

    ProjetDTO createProjet(ProjetDTO projetDTO);

    ProjetDTO getProjetById(Long id);

    List<ProjetDTO> getAllProjets();

    ProjetDTO updateProjet(Long id, ProjetDTO projetDTO);

    void deleteProjet(Long id);
}
