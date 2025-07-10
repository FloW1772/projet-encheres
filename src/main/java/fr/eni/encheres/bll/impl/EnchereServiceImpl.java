package fr.eni.encheres.bll.impl;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	public List<Enchere> rechercherEncheres(String nomArticle, int idCategorie) {
		return enchereDAO.rechercherEncheres(nomArticle, idCategorie);
	}

	@Override
	public void deleteByArticleId(long idArticle) {
		enchereDAO.deleteByArticleId(idArticle);
	}

	@Override
	@Transactional
	public void encherir(long idArticle, long idUtilisateur, int montant) throws BusinessException {

		Utilisateur utilisateur = utilisateurDAO.selectById(idUtilisateur);
		System.out.println(" Ctrl Utilisateur BLL ENCHERE " + utilisateur);
		if (utilisateur == null) {
			throw new BusinessException("Utilisateur non trouvé.");
		}
		ArticleAVendre article = articleAVendreDAO.getByID(idArticle);
		System.out.println(" Ctrl Article BLL ENCHERE " + article);
		if (article == null) {
			throw new BusinessException("Article non trouvé.");
		}

		BusinessException exception = new BusinessException();

		Enchere bestEnchere = enchereDAO.selectBestEnchereByArticle(idArticle);

		System.out.println(" Ctrl BEST Article BLL  " + bestEnchere);

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
			utilisateurDAO.crediterPoints(bestEnchere.getEncherisseur().getIdUtilisateur(), bestEnchere.getMontant());
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

	public Utilisateur getGagnant(long idArticle) {
	    Enchere meilleureEnchere = enchereDAO.selectBestEnchereByArticle(idArticle);
	    if (meilleureEnchere != null) {
	        return meilleureEnchere.getEncherisseur();
	    }
	    return null;
	}
	
	
	//----- vaut mieux creer une clkasse a part  EnchereSchedulerService
/*	  @Scheduled(fixedDelay = 60000)
	    public void traiterEncheresTerminees() {
	        List<ArticleAVendre> ventesTerminees = articleAVendreService.getVentesTermineesNonNotifiees();

	        for (ArticleAVendre article : ventesTerminees) {
	            Utilisateur gagnant = getGagnant(article.getIdArticle());
	            if (gagnant != null) {
	                notificationService.envoyerNotification(gagnant, "Votre enchère est gagnante pour l'article " + article.getNomArticle());
	            }
	            articleAVendreService.marquerCommeNotifie(article.getIdArticle());
	        }
	    }*/
	@Override
	public List<Enchere> filtrerEncheres(
	    String type,
	    boolean encheresOuvertes,
	    boolean mesEncheres,
	    boolean mesEncheresRemportees,
	    boolean ventesEnCours,
	    boolean ventesNonDebutees,
	    boolean ventesTerminees,
	    String pseudoUtilisateur) {

	    if ("achats".equals(type)) {
	        if (encheresOuvertes) {
	            return enchereDAO.getEncheresOuvertes();
	        }
	        if (mesEncheres) {
	            return enchereDAO.selectMesEncheres(pseudoUtilisateur);
	        }
	        if (mesEncheresRemportees) {
	            return enchereDAO.selectMesEncheresRemportees(pseudoUtilisateur);
	        }
	    } else if ("ventes".equals(type)) {
	        if (ventesEnCours) {
	            return enchereDAO.selectMesVentesEnCours(pseudoUtilisateur);
	        }
	        if (ventesNonDebutees) {
	            return enchereDAO.selectMesVentesNonDebutees(pseudoUtilisateur);
	        }
	        if (ventesTerminees) {
	            return enchereDAO.selectMesVentesTerminees(pseudoUtilisateur);
	        }
	    }

	    return List.of(); // Si rien n'est coché
	}
	
	
	
}

