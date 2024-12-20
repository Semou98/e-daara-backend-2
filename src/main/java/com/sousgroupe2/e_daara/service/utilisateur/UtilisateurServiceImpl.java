package com.sousgroupe2.e_daara.service.utilisateur;

import com.sousgroupe2.e_daara.DTO.ParcoursDTO;
import com.sousgroupe2.e_daara.DTO.UtilisateurDTO;
import com.sousgroupe2.e_daara.entities.parcours.Parcours;
import com.sousgroupe2.e_daara.entities.utilisateur.Utilisateur;
import com.sousgroupe2.e_daara.enums.ERole;
import com.sousgroupe2.e_daara.enums.EStatut;
import com.sousgroupe2.e_daara.mapper.ParcoursMapper;
import com.sousgroupe2.e_daara.mapper.UtilisateurMapper;
import com.sousgroupe2.e_daara.repository.parcours.ParcoursRepository;
import com.sousgroupe2.e_daara.repository.utilisateur.UtilisateurRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UtilisateurServiceImpl implements IUtilisateurService{

    private UtilisateurRepository utilisateurRepository;
    private ParcoursRepository parcoursRepository;
    private PasswordEncoder passwordEncoder;
    private UtilisateurMapper utilisateurMapper;
    private ParcoursMapper parcoursMapper;


    @Override
    public UtilisateurDTO createUtilisateur(UtilisateurDTO utilisateurDTO) {
        // Validation préalable
        validateUtilisateurCreation(utilisateurDTO);
        // Préparation de l'entité
        Utilisateur utilisateur = utilisateurMapper.toEntity(utilisateurDTO);
        // Gestion des rôles et du statut par défaut
        prepareUtilisateurRolesAndStatus(utilisateur, utilisateurDTO);
        // Encoder le mot de passe
        utilisateur.setPassword(passwordEncoder.encode(utilisateurDTO.getPassword()));
        // Sauvegarder et convertir
        Utilisateur savedUtilisateur = utilisateurRepository.save(utilisateur);
        return utilisateurMapper.toDTO(savedUtilisateur);
    }

    @Override
    public UtilisateurDTO getUtilisateurById(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Utilisateur non trouvé avec l'id : " + id));
        return utilisateurMapper.toDTO(utilisateur);
    }

    @Override
    public List<UtilisateurDTO> getAllUtilisateurs() {
        return utilisateurRepository.findAll().stream()
                .map(utilisateurMapper::toDTO)
                .collect(Collectors.toList());
    }

    public UtilisateurDTO updateUtilisateur(Long id, UtilisateurDTO utilisateurDTO) {
        // Récupérer l'utilisateur existant
        Utilisateur existingUtilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        // Mise à jour sélective des attributs
        updateUtilisateurAttributes(existingUtilisateur, utilisateurDTO);

        // Gérer les relations (parcours)
        updateUtilisateurParcours(existingUtilisateur, utilisateurDTO);

        // Sauvegarder et convertir
        Utilisateur updatedUtilisateur = utilisateurRepository.save(existingUtilisateur);
        return utilisateurMapper.toDTO(updatedUtilisateur);
    }

    @Override
    public void deleteUtilisateur(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
        // Dissocier les relations avant suppression
        utilisateur.getParcours().clear();
        utilisateurRepository.delete(utilisateur);
    }

    //*** AUTHENTICATED USER MANAGEMENT ***//

    @Override
    public UtilisateurDTO getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Utilisateur) {
            Utilisateur utilisateur = (Utilisateur) authentication.getPrincipal();
            return utilisateurMapper.toDTO(utilisateur);
        }
        throw new RuntimeException("Utilisateur non authentifié");
    }

    @Override
    public List<UtilisateurDTO> getUtilisateursByRole(String role) {
        ERole eRole = Arrays.stream(ERole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Rôle non valide : " + role));

        return utilisateurRepository.findByRolesContaining(eRole).stream()
                .map(utilisateurMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long countByRole(String role){
        ERole eRole = Arrays.stream(ERole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Rôle non valide : " + role));

        Long n =this.utilisateurRepository.countUtilisateurByRolesContaining(eRole);
        return n;
    }

    @Override
    public void updatePasswordAndEmail(Long id, String newPassword, String newEmail) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'id : " + id));
        // Mettre à jour l'email
        if (newEmail != null && !newEmail.isEmpty()) {
            utilisateur.setEmail(newEmail);
        }
        // Mettre à jour le mot de passe uniquement s'il est fourni
        if (newPassword != null && !newPassword.isEmpty()) {
            utilisateur.setPassword(passwordEncoder.encode(newPassword));
        }
        utilisateurRepository.save(utilisateur);
    }

    @Override
    public UtilisateurDTO login(String email, String password) {
        Utilisateur userLogin = utilisateurRepository.findByEmail(email);
        if (userLogin == null) {
            throw new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email);
        }
        if (passwordEncoder.matches(password, userLogin.getPassword())) {
            return utilisateurMapper.toDTO(userLogin);
        }
        throw new BadCredentialsException("Email ou mot de passe incorrect");
    }

    @Override
    public Utilisateur loadUserByEmail(String username) {
        return  this.utilisateurRepository.findByEmail(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return  this.utilisateurRepository.findByEmail(username);
    }

    //*** GESTION D'UTILISATEUR POUR UN PARCOURS ****//

    @Override
    public void addUtilisateurToParcours(Long utilisateurId, Long parcoursId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
        Parcours parcours = parcoursRepository.findById(parcoursId)
                .orElseThrow(() -> new EntityNotFoundException("Parcours non trouvé"));

        utilisateur.addParcours(parcours);
        utilisateurRepository.save(utilisateur);
    }

    @Override
    public void removeUtilisateurFromParcours(Long utilisateurId, Long parcoursId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
        Parcours parcours = parcoursRepository.findById(parcoursId)
                .orElseThrow(() -> new EntityNotFoundException("Parcours non trouvé"));

        utilisateur.removeParcours(parcours);
        utilisateurRepository.save(utilisateur);
    }

    @Override
    public List<ParcoursDTO> getParcoursUtilisateur(Long utilisateurId) {
        Utilisateur user = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        return user.getParcours().stream()
                .map(parcoursMapper::toDTO) // Supposant que vous avez un ParcoursMapper
                .collect(Collectors.toList());
    }

    //*** METHODES UTILITAIRES ****//

    private void validateUtilisateurCreation(UtilisateurDTO utilisateurDTO) {
        if (utilisateurRepository.findByEmail(utilisateurDTO.getEmail()) != null) {
            throw new EntityExistsException("Un utilisateur avec cet email existe déjà");
        }
    }

    private void prepareUtilisateurRolesAndStatus(Utilisateur utilisateur, UtilisateurDTO utilisateurDTO) {
        // Définir des rôles par défaut si non spécifiés
        Set<ERole> roles = utilisateurDTO.getRoles() != null && !utilisateurDTO.getRoles().isEmpty()
                ? utilisateurDTO.getRoles()
                : Set.of(ERole.ROLE_APPRENANT);

        utilisateur.setRoles(roles);
        utilisateur.setStatut(EStatut.ACTIF);
    }

    private void updateUtilisateurAttributes(Utilisateur existingUtilisateur, UtilisateurDTO utilisateurDTO) {
        existingUtilisateur.setPrenom(utilisateurDTO.getPrenom());
        existingUtilisateur.setNom(utilisateurDTO.getNom());
        existingUtilisateur.setEmail(utilisateurDTO.getEmail());
        existingUtilisateur.setTelephone(utilisateurDTO.getTelephone());
        // Mise à jour conditionnelle du mot de passe
        if (utilisateurDTO.getPassword() != null && !utilisateurDTO.getPassword().isEmpty()) {
            existingUtilisateur.setPassword(passwordEncoder.encode(utilisateurDTO.getPassword()));
        }
        // Autres attributs de mise à jour
        existingUtilisateur.setSexe(utilisateurDTO.getSexe());
        existingUtilisateur.setNationalite(utilisateurDTO.getNationalite());
        existingUtilisateur.setPaysResidence(utilisateurDTO.getPaysResidence());
    }

    private void updateUtilisateurParcours(Utilisateur utilisateur, UtilisateurDTO utilisateurDTO) {
        // Réinitialiser les parcours existants
        utilisateur.getParcours().clear();

        // Ajouter les nouveaux parcours si des IDs sont fournis
        if (utilisateurDTO.getParcoursIds() != null && !utilisateurDTO.getParcoursIds().isEmpty()) {
            List<Parcours> nouveauxParcours = parcoursRepository.findAllById(utilisateurDTO.getParcoursIds());
            nouveauxParcours.forEach(utilisateur::addParcours);
        }
    }
}

