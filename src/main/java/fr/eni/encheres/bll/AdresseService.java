package fr.eni.encheres.bll;

import fr.eni.encheres.bo.Adresse;

public interface AdresseService {
	
	void save(Adresse adresse);
    Adresse findById(long id);
    Adresse selectAllByUtilisateurId(long idUtilisateur);
    void update(Adresse adresse);
    void delete(long id);

}
