package com.sousgroupe2.e_daara.entities.cours;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sousgroupe2.e_daara.enums.ETypeContenu;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.context.annotation.Primary;

import java.time.LocalDateTime;

@Entity @Table(name = "contenu")
@NoArgsConstructor @AllArgsConstructor @Builder @Getter @Setter @ToString(exclude = {"cours"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Contenu {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idContenuCours", nullable = false)
    private Long id;

    @NotNull
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(100)")
    private ETypeContenu typeContenu;
    
    @NotNull
    private String lienOuText;

    @Lob @Column(columnDefinition = "LONGBLOB")
    private byte[] fichierLocal;
    private String typeFichier;

    @CreationTimestamp
    @Column(updatable = false, columnDefinition = "DATETIME")
    private LocalDateTime datePublication;

    @UpdateTimestamp
    @Column(updatable = true, columnDefinition = "DATETIME")
    private LocalDateTime dateDerniereModification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_idCours")
    private Cours cours;
}
