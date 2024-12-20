package com.sousgroupe2.e_daara.controller.cours;

import com.sousgroupe2.e_daara.DTO.CoursDTO;
import com.sousgroupe2.e_daara.entities.cours.Contenu;
import com.sousgroupe2.e_daara.entities.cours.Cours;
import com.sousgroupe2.e_daara.entities.evaluations.Projet;
import com.sousgroupe2.e_daara.entities.evaluations.Quiz;
import com.sousgroupe2.e_daara.service.cours.ICoursService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cours")
@RequiredArgsConstructor
public class CoursRestController {

    private final ICoursService coursService;

    @PostMapping
    public ResponseEntity<CoursDTO> createCours(@Valid @RequestBody CoursDTO coursDTO) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(coursService.createCours(coursDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CoursDTO> updateCours(
            @PathVariable Long id,
            @Valid @RequestBody CoursDTO coursDTO) {
        return ResponseEntity.ok(coursService.updateCours(id, coursDTO));
    }

    @GetMapping
    public ResponseEntity<List<CoursDTO>> getAllCours() {
        return ResponseEntity.ok(coursService.getAllCours());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoursDTO> getCoursById(@PathVariable Long id) {
        return ResponseEntity.ok(coursService.getCoursById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCours(@PathVariable Long id) {
        coursService.deleteCours(id);
        return ResponseEntity.noContent().build();
    }
}