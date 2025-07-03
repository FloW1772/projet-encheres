package fr.eni.encheres.bll;

import java.util.List;

import fr.eni.encheres.bo.Categorie;

public interface CategorieService {
	
	 List<Categorie> getAllCategories();
	 
	 void ajouterCategorie(Categorie categorie);
	 
	 
	 
	}

	
