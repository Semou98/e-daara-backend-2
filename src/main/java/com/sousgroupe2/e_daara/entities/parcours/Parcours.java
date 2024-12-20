package com.sousgroupe2.e_daara.entities.parcours;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sousgroupe2.e_daara.entities.cours.Contenu;
import com.sousgroupe2.e_daara.entities.cours.Cours;
import com.sousgroupe2.e_daara.entities.utilisateur.Utilisateur;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity @Table(name = "parcours")
@NoArgsConstructor @AllArgsConstructor @Builder
@Getter @Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Parcours {

    @Id
    @Column(name = "idParcours", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String nom;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String difficulte;

    @Column(columnDefinition = "TEXT")
    private String prerequis;

    @Column(name = "duree", columnDefinition = "VARCHAR(255)")
    private String dureeString;

    @Transient
    private Period duree;

    // Conversion de durée
    @PostLoad
    public void convertDureeStringToPeriod() {
        this.duree = StringUtils.hasText(this.dureeString)
                ? Period.parse(this.dureeString)
                : null;
    }

    @Column(columnDefinition = "DATETIME")
    private LocalDate dateDebut;

    @Column(columnDefinition = "DATETIME")
    private LocalDate dateFin;

    @Column(precision = 4, scale = 1, columnDefinition = "DECIMAL(4,1)")
    private BigDecimal progression;

    @Lob @Column(columnDefinition = "LONGBLOB")
    private byte[] image;
    private String imageType;

    //Relations avec d'autres entités

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_idCategorie")
    private Categorie categorie;

    @ManyToMany(fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(
            name = "utilisateur_has_parcours",
            joinColumns = @JoinColumn(name = "fk_idParcours"),
            inverseJoinColumns = @JoinColumn(name = "fk_idUtilisateur")
    )
    private List<Utilisateur> utilisateurs = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    @JoinTable(name = "parcours_has_cours",
            joinColumns = @JoinColumn(name = "fk_idParcours"),
            inverseJoinColumns = @JoinColumn(name = "fk_idCours"))
    private List<Cours> cours = new ArrayList<>();

    // Méthodes utilitaires pour gérer la relation avec Utilisateur
    public void addUtilisateur(Utilisateur utilisateur) {
        if (utilisateur != null && !this.utilisateurs.contains(utilisateur)) {
            this.utilisateurs.add(utilisateur);
            utilisateur.getParcours().add(this);
        }
    }

    public void addAllUtilisateurs(List<Utilisateur> nouveauxUtilisateurs) {
        nouveauxUtilisateurs.forEach(this::addUtilisateur);
    }

    public void removeUtilisateur(Utilisateur utilisateur) {
        if (utilisateur != null && this.utilisateurs.contains(utilisateur)) {
            this.utilisateurs.remove(utilisateur);
            utilisateur.getParcours().remove(this);
        }
    }

    public void clearUtilisateurs() {
        this.utilisateurs.forEach(utilisateur ->
                utilisateur.getParcours().remove(this));
        this.utilisateurs.clear();
    }

    // Méthodes utilitaires pour gérer la relation avec Cours
    public void addCours(Cours cours) {
        if (!this.cours.contains(cours)) {
            this.cours.add(cours);
            cours.getParcours().add(this);
        }
    }

    public void removeCours(Cours cours) {
        if (this.cours.contains(cours)) {
            this.cours.remove(cours);
            cours.getParcours().remove(this);
        }
    }

    public void clearCours() {
        this.cours.forEach(cours ->
                cours.getParcours().remove(this));
        this.cours.clear();
    }

    //equals et hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parcours parcours = (Parcours) o;
        return Objects.equals(id, parcours.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
