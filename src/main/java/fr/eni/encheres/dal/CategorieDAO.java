package fr.eni.encheres.dal;

import java.util.List;

import fr.eni.encheres.bo.Categorie;

public interface CategorieDAO {
	
	List<Categorie> findAll();
	
	void insert(Categorie categorie);

	Categorie findById(int idCategorie);
}
