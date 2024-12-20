package com.sousgroupe2.e_daara.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategorieDTO {
    private Long id;
    private String nom;
    private String description;
    private byte[] image;
    private List<Long> parcoursIds;

    public CategorieDTO(Long id) {
        this.id = id;
    }
}