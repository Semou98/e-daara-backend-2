package com.sousgroupe2.e_daara.entities.utilisateur;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.annotation.*;
import com.sousgroupe2.e_daara.entities.parcours.Parcours;
import com.sousgroupe2.e_daara.enums.ERole;
import com.sousgroupe2.e_daara.enums.ESexe;
import com.sousgroupe2.e_daara.enums.EStatut;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Entity @Table(name = "utilisateur")
@NoArgsConstructor @AllArgsConstructor @Builder @Getter @Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Utilisateur implements UserDetails {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUtilisateur", nullable = false)
    private Long id;

    @Column(nullable = false)
    public String prenom;

    @Column(nullable = false)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexe", nullable = false, columnDefinition = "VARCHAR(255)")
    private ESexe sexe;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false) @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String telephone;
    private String roles;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(255)")
    private EStatut statut = EStatut.INACTIF;

    @Column(nullable = false, columnDefinition = "LONGBLOB")
    @Lob
    private byte[] image;

    @Column(columnDefinition = "LONGBLOB")
    @Lob
    private byte[] photo;
    private String typePhoto;

    @Column(columnDefinition = "DATETIME")
    private LocalDate dateNaissance;

    private String nationalite;
    private String paysResidence;

    @CreationTimestamp
    @Column(updatable = false, columnDefinition = "DATETIME")
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Column(updatable = true, columnDefinition = "DATETIME")
    private LocalDateTime dateDerniereModification;

    @Override
    public String getUsername() {
        //return this.email;
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        //return UserDetails.super.isAccountNonExpired();
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        //return UserDetails.super.isAccountNonLocked();
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        //return UserDetails.super.isCredentialsNonExpired();
        return true;
    }

    @Override
    public boolean isEnabled() {
        //return UserDetails.super.isEnabled();
        //return this.statut!= EStatut.INACTIF && this.statut != EStatut.SUSPENDU;
        return true;
    }

    //*** Gestion des roles et des autorités ***//

    //Setter pour roles: converti la collection de rôles en chaîne de caractères (ex: "ADMIN, APPRENANT")
    public void setRoles(Set<ERole> roles) {
        this.roles = roles.stream()
                .map(Enum::name)
                .collect(Collectors.joining(","));
    }

    //Getter pour roles: récupére les rôles à partir de la chaîne de caractères
    public Set<ERole> getRoles() {
        return Optional.ofNullable(this.roles)
                .filter(r -> !r.isEmpty())
                .map(r -> Arrays.stream(r.split(","))
                        .map(ERole::valueOf)
                        .collect(Collectors.toSet()))
                .orElse(Set.of(ERole.ROLE_APPRENANT));
    }
    /*public Set<ERole> getRoles() {
        if (this.roles == null || this.roles.isEmpty()) {
            return new HashSet<>();
        }
        return Arrays.stream(this.roles.split(","))
                .map(ERole::valueOf)
                .collect(Collectors.toSet());
    }*/

    //Getter des Authorities: transforme les roles en une liste de SimpleGrantedAuthority
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList());
    }
    /*@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> _roles = new ArrayList<>();
        for(ERole role: this.getRoles()) {
            //_roles.add(new SimpleGrantedAuthority("ROLE_"+role.name()));
            _roles.add(new SimpleGrantedAuthority(role.name()));
        }
        return _roles;
    }*/

    /*@Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
    }*/

    // Méthodes utilitaires supplémentaires pour les roles
    public boolean hasRole(ERole role) {
        return getRoles().contains(role);
    }

    public void addRole(ERole role) {
        Set<ERole> currentRoles = getRoles();
        currentRoles.add(role);
        setRoles(currentRoles);
    }

    public void removeRole(ERole role) {
        Set<ERole> currentRoles = getRoles();
        currentRoles.remove(role);
        setRoles(currentRoles);
    }

    // Relations avec d'autres entités

    @ManyToMany(mappedBy = "utilisateurs", fetch = FetchType.LAZY)
    private List<Parcours> parcours = new ArrayList<>();

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private List<NewPassword> newPasswords = new ArrayList<>();

    // Methodes utilitaires pour gérer les relations avec d'autres entités
    public void addParcours(Parcours parcours) {
        if (!this.parcours.contains(parcours)) {
            this.parcours.add(parcours);
            parcours.addUtilisateur(this);
        }
    }

    public void removeParcours(Parcours parcours) {
        if (this.parcours.contains(parcours)) {
            this.parcours.remove(parcours);
            parcours.removeUtilisateur(this);
        }
    }

    public void clearParcours() {
        this.parcours.forEach(parcours ->
                parcours.getUtilisateurs().remove(this));
        this.parcours.clear();
    }
}
