package com.sousgroupe2.e_daara.service.utilisateur;

import com.sousgroupe2.e_daara.DTO.ParcoursDTO;
import com.sousgroupe2.e_daara.DTO.UtilisateurDTO;
import com.sousgroupe2.e_daara.entities.parcours.Parcours;
import com.sousgroupe2.e_daara.entities.utilisateur.Utilisateur;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface IUtilisateurService extends UserDetailsService {
    // Opérations CRUD
    UtilisateurDTO createUtilisateur(UtilisateurDTO utilisateurDTO);
    UtilisateurDTO updateUtilisateur(Long id, UtilisateurDTO utilisateurDTO);
    void deleteUtilisateur(Long id);

    // Récupération
    UtilisateurDTO getUtilisateurById(Long id);
    List<UtilisateurDTO> getAllUtilisateurs();
    List<UtilisateurDTO> getUtilisateursByRole(String role);

    // Authentification et Profil
    UtilisateurDTO getAuthenticatedUser();

    void updatePasswordAndEmail(Long id, String newPassword, String newEmail);

    UtilisateurDTO login(String email, String password);

    Utilisateur loadUserByEmail(String username);
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    // Gestion des Parcours
    void addUtilisateurToParcours(Long utilisateurId, Long parcoursId);
    void removeUtilisateurFromParcours(Long utilisateurId, Long parcoursId);
    List<ParcoursDTO> getParcoursUtilisateur(Long utilisateurId);

    // Statistiques
    Long countByRole(String role);
}