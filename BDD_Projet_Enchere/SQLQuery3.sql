ALTER TABLE Utilisateur ALTER COLUMN idAdresse BIGINT NULL;
 
 
ALTER TABLE Utilisateur

ADD CONSTRAINT fk_utilisateur_adresse

FOREIGN KEY (idAdresse) REFERENCES Adresse(idAdresse);
 
ALTER TABLE Utilisateur DROP CONSTRAINT fk_utilisateur_adresse 
 