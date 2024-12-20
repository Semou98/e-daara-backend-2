package com.sousgroupe2.e_daara.controller.cours;

import com.sousgroupe2.e_daara.DTO.ContenuDTO;
import com.sousgroupe2.e_daara.entities.cours.Contenu;
import com.sousgroupe2.e_daara.service.contenu.IContenuService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
@RequestMapping("/contenu")
@RequiredArgsConstructor
public class ContenuRestController {

    private final IContenuService contenuService;

    @PostMapping
    public ResponseEntity<ContenuDTO> createContenu(@Valid @RequestBody ContenuDTO contenuDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(contenuService.createContenu(contenuDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContenuDTO> updateContenu(
            @PathVariable Long id,
            @Valid @RequestBody ContenuDTO contenuDTO) {
        return ResponseEntity.ok(contenuService.updateContenu(id, contenuDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContenuDTO> getContenuById(@PathVariable Long id) {
        return ResponseEntity.ok(contenuService.getContenuById(id));
    }

    @GetMapping
    public ResponseEntity<List<ContenuDTO>> getAllContenu() {
        return ResponseEntity.ok(contenuService.getAllContenu());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContenu(@PathVariable Long id) {
        contenuService.deleteContenu(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<ContenuDTO> uploadFile(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(contenuService.uploadFile(id, file));
    }
}
