DROP TABLE IF EXISTS Enchere;

DROP TABLE IF EXISTS ArticleAVendre;

DROP TABLE IF EXISTS Categorie;

DROP TABLE IF EXISTS Utilisateur_Role;

DROP TABLE IF EXISTS Utilisateur;

DROP TABLE IF EXISTS Role;

DROP TABLE IF EXISTS Adresse;
 
 
-- Création de la table Adresse

CREATE TABLE Adresse (

    idAdresse BIGINT IDENTITY(1,1) PRIMARY KEY,

    rue NVARCHAR(250) NOT NULL,

    codePostal NVARCHAR(10) NOT NULL,

    ville NVARCHAR(100) NOT NULL,

    pays NVARCHAR(100) NOT NULL

);
 
 
-- Création de la table Role

CREATE TABLE Role (

    idRole INT IDENTITY(1,1) PRIMARY KEY,

    libelle NVARCHAR(50) NOT NULL UNIQUE

);
 
 
-- Création de la table Utilisateur avec idUtilisateur en BIGINT

CREATE TABLE Utilisateur (

    idUtilisateur BIGINT IDENTITY(1,1) PRIMARY KEY,

    pseudo NVARCHAR(50) NOT NULL UNIQUE,

    nom NVARCHAR(100) NOT NULL,

    prenom NVARCHAR(100) NOT NULL,

    email NVARCHAR(255) NOT NULL UNIQUE,

    telephone NVARCHAR(20),

    motDePasse NVARCHAR(255) NOT NULL,

    credit INT NOT NULL DEFAULT 100,

    idAdresse BIGINT NULL,

    CONSTRAINT fk_utilisateur_adresse FOREIGN KEY (idAdresse) REFERENCES Adresse(idAdresse) ON DELETE NO ACTION

);
 
 
-- Création de la table Utilisateur_Role avec idUtilisateur en BIGINT

CREATE TABLE Utilisateur_Role (

    idUtilisateur BIGINT NOT NULL,

    idRole INT NOT NULL,

    PRIMARY KEY (idUtilisateur, idRole),

    FOREIGN KEY (idUtilisateur) REFERENCES Utilisateur(idUtilisateur) ON DELETE CASCADE,

    FOREIGN KEY (idRole) REFERENCES Role(idRole) ON DELETE CASCADE

);
 
 
-- Création de la table Categorie

CREATE TABLE Categorie (

    idCategorie INT IDENTITY(1,1) PRIMARY KEY,

    libelle NVARCHAR(100) NOT NULL UNIQUE

);
 
 
-- Création de la table ArticleAVendre avec idUtilisateur en BIGINT

CREATE TABLE ArticleAVendre (

    idArticle INT IDENTITY(1,1) PRIMARY KEY,

    nomArticle NVARCHAR(100) NOT NULL,

    description NVARCHAR(400) NOT NULL,

    dateDebutEncheres DATETIME NOT NULL,

    dateFinEncheres DATETIME NOT NULL,

    miseAPrix INT NOT NULL,

    prixVente INT,

    etatVente NVARCHAR(20) NOT NULL, 

    idUtilisateur BIGINT NOT NULL, 

    idCategorie INT NULL,

    idAdresseRetrait BIGINT NOT NULL,

    FOREIGN KEY (idUtilisateur) REFERENCES Utilisateur(idUtilisateur) ON DELETE CASCADE,

    FOREIGN KEY (idCategorie) REFERENCES Categorie(idCategorie) ON DELETE SET NULL,

    FOREIGN KEY (idAdresseRetrait) REFERENCES Adresse(idAdresse)

);
 
 
-- Création de la table Enchere avec idUtilisateur en BIGINT

CREATE TABLE Enchere (

    idEnchere INT IDENTITY(1,1) PRIMARY KEY,

    dateEnchere DATETIME NOT NULL,

    montant INT NOT NULL,

    idUtilisateur BIGINT NOT NULL,

    idArticle INT NOT NULL,

    FOREIGN KEY (idUtilisateur) REFERENCES Utilisateur(idUtilisateur) ON DELETE CASCADE,

    FOREIGN KEY (idArticle) REFERENCES ArticleAVendre(idArticle) ON DELETE NO ACTION

);
 
 
ALTER TABLE Adresse

ADD idUtilisateur BIGINT NULL;
 
ALTER TABLE Adresse

ADD CONSTRAINT fk_adresse_utilisateur

FOREIGN KEY (idUtilisateur) REFERENCES Utilisateur(idUtilisateur);
 