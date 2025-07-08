package fr.eni.encheres.dal;

import java.util.List;

import fr.eni.encheres.bo.ArticleAVendre;
import fr.eni.encheres.exception.BusinessException;

public interface ArticleAVendreDAO {

	ArticleAVendre getByID(long id);

	void addArticle(ArticleAVendre articleAVendre);
	
	void updatePrixVente(long id, int montant);
	
	List<ArticleAVendre> findAllStatutEnCours();
	
	List<ArticleAVendre> findAllByUtilisateur(String pseudo);
	
	int annulerVente(long id)throws BusinessException;
	
	void updateArticle(ArticleAVendre articleAVendre);

	List<ArticleAVendre> findAllWithParameters(String nomRecherche, int categorieRecherche, int statutRecherche, int casUtilisationFiltres, String pseudoUtilisateurEnSession);
	
	int livrerVente(long id);

	int activerVente();

	int cloturerVente();

	int trouverProprietaireArticle(int idArticle, String pseudo);

	 List<ArticleAVendre> rechercherArticlesEnCours(String nomRecherche, int categorieRecherche);

	List<ArticleAVendre> findArticlesEnCoursByVendeur(String pseudo);
	
	
}
