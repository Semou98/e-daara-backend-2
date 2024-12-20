package com.sousgroupe2.e_daara.entities.utilisateur;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity @Table(name = "newpassword")
@NoArgsConstructor @AllArgsConstructor @Builder @Getter @Setter
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class NewPassword {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @Column(columnDefinition = "DATETIME")
    private Date date=new Date();

    @Column(columnDefinition = "DATETIME")
    private Date expirationDate= new Date(date.getTime()+1000*60*60*24);

    @Column(columnDefinition = "TINYINT(1)") // 0 : False; 1 : True.
    private boolean valid=true;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "fk_idUtilisateur")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Utilisateur utilisateur;
}
