package com.sousgroupe2.e_daara.repository.utilisateur;

import com.sousgroupe2.e_daara.entities.utilisateur.Utilisateur;

import com.sousgroupe2.e_daara.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    //List<Utilisateur> findByRolesContainingIgnoreCase(String roleString);

    List<Utilisateur> findByRolesContaining(ERole eRole);

    Long countUtilisateurByRolesContaining(ERole eRole);

    Utilisateur findByEmail(String email);

    //Optional<Utilisateur> findByEmail(String email);
}
