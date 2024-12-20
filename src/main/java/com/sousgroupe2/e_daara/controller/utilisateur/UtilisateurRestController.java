package com.sousgroupe2.e_daara.controller.utilisateur;

import com.sousgroupe2.e_daara.DTO.ParcoursDTO;
import com.sousgroupe2.e_daara.DTO.UserLogin;
import com.sousgroupe2.e_daara.DTO.UtilisateurDTO;
import com.sousgroupe2.e_daara.entities.parcours.Parcours;
import com.sousgroupe2.e_daara.entities.utilisateur.Utilisateur;
import com.sousgroupe2.e_daara.service.authentification.AuthentificationToken;
import com.sousgroupe2.e_daara.service.utilisateur.INewPassword;
import com.sousgroupe2.e_daara.service.utilisateur.IUtilisateurService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = "/utilisateurs")
@AllArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"})
public class UtilisateurRestController {

    private final IUtilisateurService utilisateurService;
    private final AuthenticationManager authenticationManager;
    private final AuthentificationToken authentificationToken;
    private final AuthentificationToken jwtService;
    private final INewPassword newPassword;

    @PostMapping
    public ResponseEntity<UtilisateurDTO> createUtilisateur(@RequestBody UtilisateurDTO utilisateurDTO) {
        UtilisateurDTO createdUtilisateur = utilisateurService.createUtilisateur(utilisateurDTO);
        return new ResponseEntity<>(createdUtilisateur, HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<UtilisateurDTO> getAuthenticatedUser() {
        UtilisateurDTO currentUser = utilisateurService.getAuthenticatedUser();
        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurDTO> getUtilisateurById(@PathVariable Long id) {
        UtilisateurDTO utilisateur = utilisateurService.getUtilisateurById(id);
        return ResponseEntity.ok(utilisateur);
    }

    @GetMapping(path ="")
    public ResponseEntity<List<UtilisateurDTO>> getAllUtilisateurs() {
        List<UtilisateurDTO> utilisateurs = utilisateurService.getAllUtilisateurs();
        return ResponseEntity.ok(utilisateurs);
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UtilisateurDTO>> getUtilisateursByRole(@PathVariable String role) {
        List<UtilisateurDTO> utilisateurs = utilisateurService.getUtilisateursByRole(role);
        return ResponseEntity.ok(utilisateurs);
    }

    @GetMapping("/count/{role}")
    public ResponseEntity<Long> CountByRole(@PathVariable String role) {
        Long count = utilisateurService.countByRole(role);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UtilisateurDTO> updateUtilisateur(@PathVariable Long id, @RequestBody UtilisateurDTO utilisateurDTO) {
        UtilisateurDTO updatedUtilisateur = utilisateurService.updateUtilisateur(id, utilisateurDTO);
        return ResponseEntity.ok(updatedUtilisateur);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUtilisateur(@PathVariable Long id) {
        utilisateurService.deleteUtilisateur(id);
        return ResponseEntity.noContent().build();
    }

    //*** GESTION DES UTILISATEURS DANS UN PARCOURS ***//

    @PostMapping("/add-to-parcours/{parcoursId}")
    public ResponseEntity<Void> addUtilisateurToParcours(@RequestBody Long utilisateurId, @PathVariable Long parcoursId) {
        utilisateurService.addUtilisateurToParcours(parcoursId, utilisateurId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{utilisateurId}/remove-parcours/{parcoursId}")
    public ResponseEntity<Void> removeUtilisateurFromParcours(@PathVariable Long utilisateurId, @PathVariable Long parcoursId) {
        utilisateurService.removeUtilisateurFromParcours(parcoursId, utilisateurId);
        return ResponseEntity.ok().build();
    }

    //*** AUTHENTIFICATION ***//

    @GetMapping("/me/parcours")
    public ResponseEntity<List<ParcoursDTO>> getMyParcours(HttpServletRequest request) {
        final String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer")) {
            final String token = auth.substring(7);
            String username = jwtService.extractusername(token);
            if(username != null){
                Utilisateur user = this.utilisateurService.loadUserByEmail(username);
                List<ParcoursDTO> parcours = this.utilisateurService.getParcoursUtilisateur(user.getId());
                return ResponseEntity.ok(parcours);
            }
        }
        return ResponseEntity.ok(Collections.emptyList());
    }

    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserLogin login) {
        if (login.email().isEmpty() || login.password().isEmpty())
            return ResponseEntity.badRequest().build();
        try {
            UtilisateurDTO user = this.utilisateurService.login(login.email(), login.password());
            String token = this.authentificationToken.genere(login.email());
            return ResponseEntity.ok(Map.of("user", user, "token", token));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/profil")
    public ResponseEntity<UtilisateurDTO> setMyProfil(HttpServletRequest request, @RequestBody UtilisateurDTO me) {
        final String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer")) {
            final String token = auth.substring(7);
            String username = jwtService.extractusername(token);
            if (username != null) {
                Utilisateur user = this.utilisateurService.loadUserByEmail(username);
                UtilisateurDTO updatedUser = this.utilisateurService.updateUtilisateur(user.getId(), me);
                return ResponseEntity.ok(updatedUser);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PutMapping("/update-credentials")
    public ResponseEntity<UtilisateurDTO> updatePasswordAndEmail(
            HttpServletRequest request,
            @RequestBody Map<String, String> credentials) {
        final String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer")) {
            final String token = auth.substring(7);
            String username = jwtService.extractusername(token);
            if (username != null) {
                Utilisateur user = this.utilisateurService.loadUserByEmail(username);
                String newPassword = credentials.get("newPassword");
                String newEmail = credentials.get("newEmail");
                this.utilisateurService.updatePasswordAndEmail(user.getId(), newPassword, newEmail);
                // Récupérer et retourner l'utilisateur mis à jour
                UtilisateurDTO updatedUser = this.utilisateurService.getUtilisateurById(user.getId());
                return ResponseEntity.ok(updatedUser);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping(path="/new-password")
    public ResponseEntity<String> getNewPassword(@RequestBody Map<String, String> email) {
        log.info("Email reçu: " + email.get("email"));
        return ResponseEntity.ok(this.newPassword.getNewPassword(email.get("email")));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path="/reset-password")
    public ResponseEntity<Boolean> verifyNewPasswordKey(@RequestBody Map<String, String> password) {
        log.info("mot de passe", password, "cle", password.get("key"));
        if (password.get("password") != null && !password.get("key").isEmpty()){
            log.info("mot de passe disponible et clé disponible");
            return ResponseEntity.ok(this.newPassword.reset(password.get("key"), password.get("password")));}
        return ResponseEntity.ok(false);
    }

    @RequestMapping(path="/verif-key/{key}")
    public ResponseEntity<Boolean> keyVerification(@PathVariable String key) {
        if (!key.isEmpty())
            return ResponseEntity.ok(this.newPassword.verifKey(key));
        return ResponseEntity.ok(false);
    }
}
