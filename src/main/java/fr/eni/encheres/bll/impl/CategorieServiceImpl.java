package fr.eni.encheres.bll.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import fr.eni.encheres.bll.CategorieService;
import fr.eni.encheres.bo.Categorie;
import fr.eni.encheres.dal.CategorieDAO;

@Service
public class CategorieServiceImpl implements CategorieService {
	
	private final CategorieDAO categorieDAO;
	
	public CategorieServiceImpl(CategorieDAO categorieDAO) {
		this.categorieDAO = categorieDAO;
	}

 
	@Override
	public List<Categorie> getAllCategories(){
		return categorieDAO.findAll();
	}
	
	@Override
	public void ajouterCategorie(Categorie categorie) {
	    categorieDAO.insert(categorie);
	}
	
}
