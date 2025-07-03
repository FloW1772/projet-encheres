package fr.eni.encheres.bll;

import java.security.Principal;
import java.util.List;

import fr.eni.encheres.bo.Adresse;
import fr.eni.encheres.bo.ArticleAVendre;
import fr.eni.encheres.bo.Categorie;
import fr.eni.encheres.bo.Utilisateur;
import fr.eni.encheres.exception.BusinessException;

public interface ArticleAVendreService {

	List<ArticleAVendre> getArticlesAVendreEnCours();

	ArticleAVendre getById(int idArticle);

	void mettreArticleEnVente(ArticleAVendre articleAVendre, Utilisateur utilisateur,int idCategorie)throws BusinessException;
	
	void modifierArticleEnVente(ArticleAVendre articleAVendre, String pseudo)throws BusinessException;
	
	Categorie getCategorieById (long id);
	
	List<Categorie> getAllCategories();
	
	Adresse getAdresseById (long id);

	List<Adresse> getAllAdressesRetrait();

	void annulerVente(ArticleAVendre article)throws BusinessException;

	List<ArticleAVendre> getArticlesAVendreAvecParamètres(String nomRecherche,
			int categorieRecherche, int casUtilisationFiltres, Principal principal);

	void activerVente();

	void cloturerVente();

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
