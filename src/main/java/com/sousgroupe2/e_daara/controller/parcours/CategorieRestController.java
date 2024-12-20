package com.sousgroupe2.e_daara.controller.parcours;

import com.sousgroupe2.e_daara.DTO.CategorieDTO;
import com.sousgroupe2.e_daara.DTO.ParcoursDTO;
import com.sousgroupe2.e_daara.entities.parcours.Categorie;
import com.sousgroupe2.e_daara.entities.parcours.Parcours;
import com.sousgroupe2.e_daara.service.categorie.ICategorieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200"})
public class CategorieRestController {

    private final ICategorieService categorieService;

    @Secured("ROLE_ADMIN")
    @PostMapping
    public ResponseEntity<CategorieDTO> createCategorie(@Valid @RequestBody CategorieDTO categorieDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categorieService.createCategorie(categorieDTO));
    }

    @GetMapping
    public ResponseEntity<List<CategorieDTO>> getAllCategories() {
        return ResponseEntity.ok(categorieService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategorieDTO> getCategorieById(@PathVariable Long id) {
        return ResponseEntity.ok(categorieService.getCategorieById(id));
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    public ResponseEntity<CategorieDTO> updateCategorie(
            @PathVariable Long id,
            @Valid @RequestBody CategorieDTO categorieDTO) {
        return ResponseEntity.ok(categorieService.updateCategorie(id, categorieDTO));
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategorie(@PathVariable Long id) {
        categorieService.deleteCategorie(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{categorieId}/parcours")
    public ResponseEntity<List<ParcoursDTO>> getParcoursByCategorie(
            @PathVariable Long categorieId) {
        return ResponseEntity.ok(categorieService.getParcoursByCategorie(categorieId));
    }
}
