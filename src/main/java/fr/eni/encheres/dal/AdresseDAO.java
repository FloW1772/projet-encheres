package fr.eni.encheres.dal;

import fr.eni.encheres.bo.Adresse;

import java.util.List;

public interface AdresseDAO {
	// CREATE
	void save(Adresse adresse);

	// READ (by ID)
	Adresse findById(long id);

	// READ (all)
	List<Adresse> findAll();

	// UPDATE
	void update(Adresse adresse);
	
	// DELETE
	void delete(long id);

	Adresse selectById(long idAdresse);

	Adresse selectAllByUtilisateurId(long idUtilisateur);

	void saveSansUtilisateur(Adresse adresse);
}
