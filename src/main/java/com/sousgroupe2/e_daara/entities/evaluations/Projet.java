package com.sousgroupe2.e_daara.entities.evaluations;

import com.fasterxml.jackson.annotation.*;
import com.sousgroupe2.e_daara.entities.cours.Cours;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jdk.jshell.Snippet;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity @Table(name = "projet")
@NoArgsConstructor @AllArgsConstructor @Builder @Getter @Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Projet{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idProjet", nullable = false)
    private Long id;

    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String difficulte;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime dateOuverture;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime dateFermeture;

    @Column(columnDefinition = "DATETIME")
    private LocalDateTime delai;

    private String urlFichier;

    @Min(0) @Max(20)
    private double note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_idCours")
    private Cours cours;

    // Méthodes utilitaires pour la gestion de la relation
    public void setCours(Cours cours) {
        if (this.cours != null) {
            this.cours.getProjets().remove(this);
        }
        this.cours = cours;
        if (cours != null && !cours.getProjets().contains(this)) {
            cours.getProjets().add(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Projet)) return false;
        Projet projet = (Projet) o;
        return Objects.equals(getId(), projet.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
