package fr.eni.encheres.bll;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Service;

import fr.eni.encheres.bo.Adresse;
import fr.eni.encheres.bo.ArticleAVendre;
import fr.eni.encheres.bo.Categorie;
import fr.eni.encheres.bo.Utilisateur;
import fr.eni.encheres.dal.AdresseDAO;
import fr.eni.encheres.dal.ArticleAVendreDAO;
import fr.eni.encheres.dal.CategorieDAO;

@Service
public class ArticleAVendreServiceImpl implements ArticleAVendreService {

	private ArticleAVendreDAO articleAVendreDAO;
	private AdresseDAO adresseDAO;
	private CategorieDAO categorieDAO;
	
	
	public ArticleAVendreServiceImpl(ArticleAVendreDAO articleAVendreDAO, AdresseDAO adresseDAO,
			CategorieDAO categorieDAO) {
		this.articleAVendreDAO = articleAVendreDAO;
		this.adresseDAO = adresseDAO;
		this.categorieDAO = categorieDAO;
	}


	@Override
	public List<ArticleAVendre> getArticlesAVendreEnCours() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ArticleAVendre getById(int idArticle) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void mettreArticleEnVente(ArticleAVendre articleAVendre, Utilisateur utilisateur) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void modifierArticleEnVente(ArticleAVendre articleAVendre, String pseudo) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Categorie getCategorieById(long id) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Categorie> getAllCategories() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Adresse getAdresseById(long id) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public List<Adresse> getAllAdressesRetrait() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void annulerVente(ArticleAVendre article) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public List<ArticleAVendre> getArticlesAVendreAvecParamètres(String nomRecherche, int categorieRecherche,
			int casUtilisationFiltres, Principal principal) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void activerVente() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void cloturerVente() {
		// TODO Auto-generated method stub
		
	}

	
	
	
	
	
	
	
	
}
