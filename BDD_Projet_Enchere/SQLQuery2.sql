DROP TABLE Enchere
DROP TABLE ArticleAVendre
DROP TABLE Categorie
DROP TABLE Utilisateur_Role
DROP TABLE Utilisateur
DROP TABLE  Role
DROP TABLE  Adresse



CREATE TABLE Adresse (
    idAdresse INT IDENTITY(1,1) PRIMARY KEY,
    rue NVARCHAR(250) NOT NULL,
    codePostal NVARCHAR(10) NOT NULL,
    ville NVARCHAR(100) NOT NULL,
    pays NVARCHAR(100) NOT NULL
);
 
CREATE TABLE Role (
    idRole INT IDENTITY(1,1) PRIMARY KEY,
    libelle NVARCHAR(50) NOT NULL UNIQUE
);
 
CREATE TABLE Utilisateur (
    idUtilisateur INT IDENTITY(1,1) PRIMARY KEY,
    pseudo NVARCHAR(50) NOT NULL UNIQUE,
    nom NVARCHAR(100) NOT NULL,
    prenom NVARCHAR(100) NOT NULL,
    email NVARCHAR(255) NOT NULL UNIQUE,
    telephone NVARCHAR(20),
    motDePasse NVARCHAR(255) NOT NULL,
    credit INT NOT NULL DEFAULT 100,
    idAdresse INT NOT NULL,
    FOREIGN KEY (idAdresse) REFERENCES Adresse(idAdresse) ON DELETE NO ACTION
);
 
CREATE TABLE Utilisateur_Role (
    idUtilisateur INT NOT NULL,
    idRole INT NOT NULL,
    PRIMARY KEY (idUtilisateur, idRole),
    FOREIGN KEY (idUtilisateur) REFERENCES Utilisateur(idUtilisateur) ON DELETE CASCADE,
    FOREIGN KEY (idRole) REFERENCES Role(idRole) ON DELETE CASCADE
);
 
CREATE TABLE Categorie (
    idCategorie INT IDENTITY(1,1) PRIMARY KEY,
    libelle NVARCHAR(100) NOT NULL UNIQUE
);
 
CREATE TABLE ArticleAVendre (
 
    idArticle INT IDENTITY(1,1) PRIMARY KEY,
    nom NVARCHAR(100) NOT NULL,
    description NVARCHAR(400) NOT NULL,
    dateDebutEncheres DATETIME NOT NULL,
    dateFinEncheres DATETIME NOT NULL,
    miseAPrix INT NOT NULL,
    prixVente INT,
    etatVente NVARCHAR(20) NOT NULL, 
    idUtilisateur INT NOT NULL, 
    idCategorie INT NULL,
    idAdresseRetrait INT NOT NULL,
    FOREIGN KEY (idUtilisateur) REFERENCES Utilisateur(idUtilisateur) ON DELETE CASCADE,
    FOREIGN KEY (idCategorie) REFERENCES Categorie(idCategorie) ON DELETE SET NULL,
    FOREIGN KEY (idAdresseRetrait) REFERENCES Adresse(idAdresse) 
);
 
CREATE TABLE Enchere (
    idEnchere INT IDENTITY(1,1) PRIMARY KEY,
    dateEnchere DATETIME NOT NULL,
    montant INT NOT NULL,
    idUtilisateur INT NOT NULL,
    idArticle INT NOT NULL,
    FOREIGN KEY (idUtilisateur) REFERENCES Utilisateur(idUtilisateur) ON DELETE CASCADE,
    FOREIGN KEY (idArticle) REFERENCES ArticleAVendre(idArticle) ON DELETE NO ACTION
);