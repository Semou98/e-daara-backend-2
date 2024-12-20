package com.sousgroupe2.e_daara.controller.parcours;

import com.sousgroupe2.e_daara.DTO.CoursDTO;
import com.sousgroupe2.e_daara.DTO.ParcoursDTO;
import com.sousgroupe2.e_daara.entities.cours.Cours;
import com.sousgroupe2.e_daara.service.parcours.IParcoursService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/parcours")
@Validated
@RequiredArgsConstructor
public class ParcoursRestController {

    private final IParcoursService parcoursService;

    @Secured("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<ParcoursDTO> createParcours(@Valid @RequestBody ParcoursDTO parcoursDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(parcoursService.createParcours(parcoursDTO));
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    public ResponseEntity<ParcoursDTO> updateParcours(
            @PathVariable Long id,
            @Valid @RequestBody ParcoursDTO parcoursDTO) {
        return ResponseEntity
                .ok(parcoursService.updateParcours(id, parcoursDTO));
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/{id}/upload-image")
    public ResponseEntity<String> uploadImage(@PathVariable Long id, @RequestParam("file") MultipartFile imageFile) {
        parcoursService.uploadImage(id, imageFile);
        return ResponseEntity.ok("Image uploadée avec succès");
    }

    @GetMapping
    public ResponseEntity<List<ParcoursDTO>> getAllParcours() {
        return ResponseEntity.ok(parcoursService.getAllParcours());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParcoursDTO> getParcoursById(@PathVariable Long id) {
        return ResponseEntity.ok(parcoursService.getParcoursById(id));
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParcours(@PathVariable Long id) {
        parcoursService.deleteParcours(id);
        return ResponseEntity.noContent().build();
    }

    // *** GESTION DES UTILISATEURS DANS UN PARCOURS *** //

    @Secured("ROLE_ADMIN")
    @PostMapping("/{parcoursId}/add-utilisateurs")
    public ResponseEntity<Void> addUtilisateursToParcours(
            @PathVariable Long parcoursId,
            @RequestBody List<Long> utilisateurIds) {
        parcoursService.addUtilisateursToParcours(parcoursId, utilisateurIds);
        return ResponseEntity.ok().build();
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{parcoursId}/delete-utilisateurs")
    public ResponseEntity<Void> removeUtilisateursFromParcours(
            @PathVariable Long parcoursId,
            @RequestBody List<Long> utilisateurIds) {
        parcoursService.removeUtilisateursFromParcours(parcoursId, utilisateurIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/cours")
    public ResponseEntity<List<CoursDTO>> getCoursByParcours(@PathVariable Long id) {
        List<Long> coursIds = parcoursService.getParcoursById(id).getCoursIds();
        return ResponseEntity.ok(coursIds.stream()
                .map(CoursDTO::new) // Placeholder for actual CoursDTO fetching logic
                .collect(Collectors.toList()));
    }
}
