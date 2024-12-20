package com.sousgroupe2.e_daara.entities.cours;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.*;
import com.sousgroupe2.e_daara.entities.evaluations.Projet;
import com.sousgroupe2.e_daara.entities.evaluations.Quiz;
import com.sousgroupe2.e_daara.entities.parcours.Parcours;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity @Table(name = "cours")
@NoArgsConstructor @AllArgsConstructor @Builder @Getter @Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Cours {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCours", nullable = false)
    private Long id;

    @NotNull
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default @Column(columnDefinition = "TINYINT")
    private Boolean isCompleted = false;

    @OneToMany(mappedBy = "cours", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<Contenu> contenus = new ArrayList<>();

    @OneToMany(mappedBy = "cours", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<Projet> projets = new ArrayList<>();

    @OneToMany(mappedBy = "cours", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    private List<Quiz> quiz = new ArrayList<>();

    @ManyToMany(mappedBy = "cours", fetch = FetchType.LAZY)
    private List<Parcours> parcours = new ArrayList<>();

    // Méthodes utilitaires pour gérer le relation avec Contenu
    public void addContenu(Contenu contenu) {
        this.contenus.add(contenu);
        contenu.setCours(this);
    }

    public void removeContenu(Contenu contenu) {
        this.contenus.remove(contenu);
        contenu.setCours(null);
    }

    public void clearContenus() {
        this.contenus.forEach(contenu -> contenu.setCours(null));
        this.contenus.clear();
    }

    public void addAllContenus(List<Contenu> nouveauxContenus) {
        nouveauxContenus.forEach(this::addContenu);
    }

    // Méthodes utilitaires pour gérer le relation avec Projet
    public void addProjet(Projet projet) {
        this.projets.add(projet);
        projet.setCours(this);
    }

    public void removeProjet(Projet projet) {
        this.projets.remove(projet);
        projet.setCours(null);
    }

    // Méthodes utilitaires pour gérer le relation avec Quiz
    public void addQuiz(Quiz quiz) {
        this.quiz.add(quiz);
        quiz.setCours(this);
    }

    public void removeQuiz(Quiz quiz) {
        this.quiz.remove(quiz);
        quiz.setCours(null);
    }

    //equals & hashCode methods
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cours cours = (Cours) o;
        return Objects.equals(id, cours.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


