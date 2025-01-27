create table Categorie (idCategorie bigint not null auto_increment, description TEXT, nom varchar(255) not null, image tinyblob, primary key (idCategorie)) engine=InnoDB;
create table Contenu (dateDerniereModification datetime(6), datePublication datetime(6), fk_idCours bigint not null, idContenuCours bigint not null auto_increment, description TEXT, lienOuText varchar(255) not null, titre varchar(255) not null, typeFichier varchar(255), fichierLocal LONGBLOB, typeContenu VARCHAR(100) not null, primary key (idContenuCours)) engine=InnoDB;
create table Cours (isCompleted bit, idCours bigint not null auto_increment, description TEXT, titre varchar(255) not null, primary key (idCours)) engine=InnoDB;
create table NewPassword (valid bit not null, date datetime(6), expirationDate datetime(6), id bigint not null auto_increment, utilisateur_id_utilisateur bigint, token varchar(255), primary key (id)) engine=InnoDB;
create table Parcours (dateDebut date, dateFin date, progression decimal(38,2), fk_idCategorie bigint, idParcours bigint not null auto_increment, description TEXT, difficulte varchar(255), duree VARCHAR(255), imageType varchar(255), nom varchar(255) not null, prerequis TEXT, image LONGBLOB, primary key (idParcours)) engine=InnoDB;
create table Parcours_has_Cours (fk_idCours bigint not null, fk_idParcours bigint not null) engine=InnoDB;
create table Projet (note float(53) not null, dateFermeture datetime(6), dateOuverture datetime(6), delai datetime(6), fk_idCours bigint, idProjet bigint not null auto_increment, description TEXT, difficulte varchar(255), titre varchar(255), urlFichier varchar(255), primary key (idProjet)) engine=InnoDB;
create table Question (points decimal(38,2), fk_idQuiz bigint not null, idQuestion bigint not null auto_increment, textQuestion TEXT, type varchar(255), primary key (idQuestion)) engine=InnoDB;
create table Quiz (duree decimal(21,0), nombreQuestion integer, note float(53) not null, dateFermeture datetime(6), dateOuverture datetime(6), delai datetime(6), fk_idCours bigint, idQuiz bigint not null auto_increment, description TEXT, difficulte varchar(255), titre varchar(255), primary key (idQuiz)) engine=InnoDB;
create table Reponse (estCorrect bit, fk_idQuestion bigint not null, idReponse bigint not null auto_increment, optionReponse TEXT, primary key (idReponse)) engine=InnoDB;
create table Utilisateur (dateNaissance date, dateCreation datetime(6), dateDerniereModification datetime(6), idUtilisateur bigint not null auto_increment, email varchar(255) not null, nationalite varchar(255), nom varchar(255) not null, password varchar(255) not null, paysResidence varchar(255), prenom varchar(255) not null, roles varchar(255), telephone varchar(255), typePhoto varchar(255), image longblob, photo LONGBLOB, sexe enum ('F','M') not null, statut enum ('ACTIF','CONNECTE','DECONNECTE','EN_ATTENTE_DE_VALIDATION','INACTIF','SUSPENDU'), primary key (idUtilisateur)) engine=InnoDB;
create table Utilisateur_has_Parcours (fk_idParcours bigint not null, fk_idUtilisateur bigint not null) engine=InnoDB;

alter table Utilisateur add constraint UK35ysk0sh9ruwixrld3nc0weut unique (email);
alter table Contenu add constraint FK4rrlnvutp0qbnexwdfujy468f foreign key (fk_idCours) references Cours (idCours);
alter table NewPassword add constraint FK528egbtktf48dy4pvvr7d3vps foreign key (utilisateur_id_utilisateur) references Utilisateur (idUtilisateur);
alter table Parcours add constraint FKgiw0hu2ph6pl2h0jltq2jntq7 foreign key (fk_idCategorie) references Categorie (idCategorie);
alter table Parcours_has_Cours add constraint FKfx8qa8ch0vmh4f3px7vehsneb foreign key (fk_idCours) references Cours (idCours);
alter table Parcours_has_Cours add constraint FKteew1xn98m8wcp4s4k3u5xn3u foreign key (fk_idParcours) references Parcours (idParcours);
alter table Projet add constraint FKt7pkjceljrfdndcwbn0205qp1 foreign key (fk_idCours) references Cours (idCours);
alter table Question add constraint FKlmhgmbu8lve41p0pqc64ekkim foreign key (fk_idQuiz) references Quiz (idQuiz);
alter table Quiz add constraint FKexewaxdj2s8txvs2s075iarsx foreign key (fk_idCours) references Cours (idCours);
alter table Reponse add constraint FKcavxfrq7mpwabkdjbnat1f1ih foreign key (fk_idQuestion) references Question (idQuestion);
alter table Utilisateur_has_Parcours add constraint FKckiaeubdf9ws573tgs30res8y foreign key (fk_idParcours) references Parcours (idParcours);
alter table Utilisateur_has_Parcours add constraint FKa6if5107skm165s7vw97wce4 foreign key (fk_idUtilisateur) references Utilisateur (idUtilisateur);
