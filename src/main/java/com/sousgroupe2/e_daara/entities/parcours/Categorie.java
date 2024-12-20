package com.sousgroupe2.e_daara.entities.parcours;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity @Table(name = "categorie")
@NoArgsConstructor @AllArgsConstructor @Builder @Getter @Setter @ToString(exclude = {"parcours"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Categorie {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCategorie", nullable = false)
    private Long id;

    @NotNull
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Lob @Column(columnDefinition = "LONGBLOB")
    private byte[] image = null;

    @OneToMany(mappedBy = "categorie", cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY)
    private List<Parcours> parcours = new ArrayList<>();

    // Méthodes utilitaires
    public void addParcours(Parcours parcours) {
        this.parcours.add(parcours);
        parcours.setCategorie(this);
    }

    public void removeParcours(Parcours parcours) {
        this.parcours.remove(parcours);
        parcours.setCategorie(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Categorie categorie = (Categorie) o;
        return Objects.equals(id, categorie.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
