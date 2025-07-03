package fr.eni.encheres.bll.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import fr.eni.encheres.bll.EnchereService;
import fr.eni.encheres.bo.ArticleAVendre;
import fr.eni.encheres.bo.Enchere;
import fr.eni.encheres.bo.Utilisateur;
import fr.eni.encheres.dal.ArticleAVendreDAO;
import fr.eni.encheres.dal.EnchereDAO;
import fr.eni.encheres.dal.UtilisateurDAO;
import fr.eni.encheres.exception.BusinessException;

@Service
public class EnchereServiceImpl implements EnchereService {

	private final ArticleAVendreDAO articleAVendreDAO;
	private final EnchereDAO enchereDAO;
	private final UtilisateurDAO utilisateurDAO;

	public EnchereServiceImpl(EnchereDAO enchereDAO, UtilisateurDAO utilisateurDAO,
			ArticleAVendreDAO articleAVendreDAO) {
		this.enchereDAO = enchereDAO;
		this.utilisateurDAO = utilisateurDAO;
		this.articleAVendreDAO = articleAVendreDAO;
	}

	@Override
	public void create(Enchere enchere) {
		enchereDAO.create(enchere);
	}

	@Override
	public Enchere readById(long idEnchere) {
		return enchereDAO.readById(idEnchere);
	}

	@Override
	public List<Enchere> readAll() {
		return enchereDAO.readAll();
	}

	@Override
	public void update(Enchere enchere) {
		enchereDAO.update(enchere);
	}

	@Override
	public void delete(long idEnchere) {
		enchereDAO.delete(idEnchere);
	}

	@Override
	public Enchere selectBestEnchereByArticle(long idArticle) {
		return enchereDAO.selectBestEnchereByArticle(idArticle);
	}

	@Override
	public List<Enchere> selectEncheresByUtilisateur(long idUtilisateur) {
		return enchereDAO.selectEncheresByUtilisateur(idUtilisateur);
	}

	@Override
	public List<Enchere> selectEncheresByArticle(long idArticle) {
		return enchereDAO.selectEncheresByArticle(idArticle);
	}

	@Override
	public void deleteByArticleId(long idArticle) {
		enchereDAO.deleteByArticleId(idArticle);
	}

	@Override
	public void encherir(long idArticle, long idUtilisateur, int montant) throws BusinessException {

		Utilisateur utilisateur = utilisateurDAO.selectById(idUtilisateur);
		ArticleAVendre article = articleAVendreDAO.getByID(idArticle);

		BusinessException exception = new BusinessException();

		Enchere bestEnchere = enchereDAO.selectBestEnchereByArticle(idArticle);

		// On verifie que l'enchérisseur n'est pas le vendeur
		if (article.getVendeur().getIdUtilisateur() == utilisateur.getIdUtilisateur()) {
			exception.add(" Vous ne pouvez pas enchérir sur votre propre article");
		}

		// on vérifie que le montant de l'enchere > prix actuel
		int prixActuel = (article.getPrixVente() > 0) ? article.getPrixVente() : article.getMiseAPrix();
		if (montant <= prixActuel) {
			exception.add("Votre enchère doit être supérieure au prix actuel");
		}

		// on vérifie que l'utilisateur à assez de points
		if (utilisateur.getCredit() < montant) {
			exception.add("Crédit insuffisant pour effectuer cette enchère");
		}

		// si il y a des erreurs on arrête ici
	    if (exception.hasError()) {
	        throw exception;
	    }

	    // 5. Récupère l'ancienne enchère et rembourse si nécessaire
		if (bestEnchere != null) {
			utilisateurDAO.crediterPoints(
				bestEnchere.getEncherisseur().getIdUtilisateur(),
				bestEnchere.getMontant()
			);
		}

		// on débite le nouvel enchérisseur
		utilisateurDAO.debiterPoints(idUtilisateur, montant);

		// On crée et enregistre la nouvelle enchère
	    Enchere nouvelleEnchere = new Enchere();
	    nouvelleEnchere.setArticle(article);
	    nouvelleEnchere.setEncherisseur(utilisateur);
	    nouvelleEnchere.setMontant(montant);
	    enchereDAO.create(nouvelleEnchere); 

	    // On met à jour le prix de vente de l’article
	    articleAVendreDAO.updatePrixVente(idArticle, montant);
	}
}