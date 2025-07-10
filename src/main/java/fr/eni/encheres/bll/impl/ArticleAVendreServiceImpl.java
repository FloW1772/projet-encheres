package fr.eni.encheres.bll.impl;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.eni.encheres.bll.ArticleAVendreService;
import fr.eni.encheres.bo.Adresse;
import fr.eni.encheres.bo.ArticleAVendre;
import fr.eni.encheres.bo.Categorie;
import fr.eni.encheres.bo.EtatVente;
import fr.eni.encheres.bo.Utilisateur;
import fr.eni.encheres.dal.AdresseDAO;
import fr.eni.encheres.dal.ArticleAVendreDAO;
import fr.eni.encheres.dal.CategorieDAO;
import fr.eni.encheres.exception.BusinessException;

@Service
public class ArticleAVendreServiceImpl implements ArticleAVendreService {

	private final ArticleAVendreDAO articleAVendreDAO;
	private final AdresseDAO adresseDAO;
	private final CategorieDAO categorieDAO;

	public ArticleAVendreServiceImpl(ArticleAVendreDAO articleAVendreDAO, AdresseDAO adresseDAO,
			CategorieDAO categorieDAO) {
		this.articleAVendreDAO = articleAVendreDAO;
		this.adresseDAO = adresseDAO;
		this.categorieDAO = categorieDAO;
	}

	@Override
	@Transactional
	public void mettreArticleEnVente(ArticleAVendre articleAVendre, Utilisateur utilisateur, int idCategorie)
			throws BusinessException {
		BusinessException be = new BusinessException();

		articleAVendre.setVendeur(utilisateur);
		articleAVendre.setCategorie(categorieDAO.findById(idCategorie));

		Adresse adresseRetrait = articleAVendre.getAdresseRetrait();

		if (adresseRetrait != null) {
			if (adresseRetrait.getIdAdresse() <= 0) {
				try {
					adresseDAO.saveSansUtilisateur(adresseRetrait);
				} catch (Exception e) {
					be.add("L'adresse de retrait fournie est invalide.");
				}
			} else {
				Adresse adresseComplete = adresseDAO.findById(adresseRetrait.getIdAdresse());
				if (adresseComplete == null) {
					be.add("L'adresse de retrait n'existe pas en base.");
				} else {
					articleAVendre.setAdresseRetrait(adresseComplete);
				}
			}
		} else {
			be.add("L'adresse de retrait est obligatoire.");
		}

		if (validerArticleAVendre(articleAVendre, be)) {
			if (be.hasError()) {
				throw be;
			}
			try {
				articleAVendreDAO.addArticle(articleAVendre);
			} catch (DataAccessException e) {
				e.printStackTrace();
				be.add("Erreur technique lors de l'ajout de l'article.");
				throw be;
			}
		} else {
			throw be;
		}
	}

	@Override
	public void modifierArticleEnVente(ArticleAVendre articleAVendre, String pseudo) throws BusinessException {
		BusinessException be = new BusinessException();
		if (validerArticleAVendre(articleAVendre, be)) {
			Adresse adresseRetrait = articleAVendre.getAdresseRetrait();
			if (adresseRetrait != null) {
				if (adresseRetrait.getIdAdresse() > 0) {
					adresseDAO.update(adresseRetrait);
				} else {
					adresseDAO.saveSansUtilisateur(adresseRetrait);
					articleAVendre.setAdresseRetrait(adresseRetrait);
				}
			}

			Utilisateur utilisateur = new Utilisateur();
			utilisateur.setPseudo(pseudo);
			articleAVendre.setVendeur(utilisateur);
			articleAVendreDAO.updateArticle(articleAVendre);
		} else {
			throw be;
		}
	}

	private boolean validerArticleAVendre(ArticleAVendre articleAVendre, BusinessException be) {
		if (articleAVendre == null) {
			be.add("Article inexistant");
			return false;
		}
		// Validations supplémentaires ici
		return true;
	}

	private boolean validerNom(String nom, BusinessException be) {
		if (nom == null || nom.isBlank()) {
			be.add("Le nom de l'article est incorrect");
			return false;
		}
		if (nom.length() < 5 || nom.length() > 30) {
			be.add("Le nom doit être compris entre 5 et 30 caractères");
			return false;
		}
		return true;
	}

	private boolean validerDescription(String description, BusinessException be) {
		if (description == null || description.isBlank()) {
			be.add("La description est obligatoire");
			return false;
		}
		if (description.length() < 20 || description.length() > 300) {
			be.add("La description doit être comprise entre 20 et 300 caractères");
			return false;
		}
		return true;
	}

	@Override
	public List<ArticleAVendre> getArticlesAVendreEnCours() {
		return articleAVendreDAO.findAllStatutEnCours();
	}

	@Override
	public List<ArticleAVendre> getArticlesAVendreAvecParamètres(String nomRecherche, int categorieRecherche,
			int casUtilisationFiltres, Principal principal) {
		// Implémentation manquante, à compléter si nécessaire
		return null;
	}

	private boolean validerDateDebutEncheres(LocalDate dateDebutEncheres, BusinessException be) {
		LocalDate today = LocalDate.now();
		if (dateDebutEncheres == null) {
			be.add("La date de début de l'enchère est incorrecte");
			return false;
		}
		if (dateDebutEncheres.isBefore(today)) {
			be.add("La date de début de l'enchère ne peut pas être dans le passé");
			return false;
		}
		return true;
	}

	private boolean validerDateFinEncheres(LocalDate dateFinEncheres, LocalDate dateDebutEncheres,
			BusinessException be) {
		if (dateFinEncheres == null) {
			be.add("La date de fin de l'enchère est obligatoire");
			return false;
		}
		if (dateFinEncheres.isBefore(dateDebutEncheres)) {
			be.add("La date de fin de l'enchère ne peut pas être avant la date de début");
			return false;
		}
		return true;
	}

	private boolean validerPrixInitial(int prixInitial, BusinessException be) {
		if (prixInitial < 1) {
			be.add("Le prix initial doit être supérieur à zéro");
			return false;
		}
		return true;
	}

	private boolean validerAdresseRetrait(Adresse adresseRetrait, BusinessException be) {
		if (adresseRetrait == null) {
			be.add("L'adresse de retrait est obligatoire");
			return false;
		}
		if (adresseRetrait.getIdAdresse() <= 0) {
			be.add("L'adresse de retrait n'est pas valide");
			return false;
		}
		Adresse adresseEnBase = this.adresseDAO.findById(adresseRetrait.getIdAdresse());
		if (adresseEnBase == null) {
			be.add("L'adresse de retrait n'existe pas en base");
			return false;
		}
		return true;
	}

	private boolean validerCategorie(Categorie categorie, BusinessException be) {
		if (categorie == null) {
			be.add("La catégorie est obligatoire");
			return false;
		}
		if (categorie.getIdCategorie() <= 0) {
			be.add("La catégorie n'est pas valide");
			return false;
		}
		return true;
	}

	@Override
	public void annulerVente(ArticleAVendre article) throws BusinessException {
		
		BusinessException be = new BusinessException();

		boolean isValid = true;
		isValid &= validerAnnulationDateDebutEnchere(article.getDateDebutEncheres(), be);
		isValid &= validerStatutVente(article.getEtatVente(), be);

		if (isValid) {
			int count = this.articleAVendreDAO.annulerVente(article.getIdArticle());
			if (count == 0) {
				be.add("L'annulation de la vente a échoué");
				throw be;
			}
		} else {
			throw be;
		}

	}

	private boolean validerAnnulationDateDebutEnchere(LocalDateTime dateDebutEncheres, BusinessException be) {
		LocalDateTime today = LocalDateTime.now();
		if (dateDebutEncheres.isBefore(today) || dateDebutEncheres.equals(today)) {
			be.add("La vente ne peut être annulée que si l'enchère n'a pas encore commencé");
			return false;
		}
		return true;
	}

	private boolean validerStatutVente(EtatVente etatVente, BusinessException be) {
		if (etatVente == EtatVente.EN_COURS) {
			be.add("La vente ne peut être annulée car elle est déjà en cours");
			return false;
		}
		return true;
	}

	 // Toutes les nuits à 1h du matin
    @Scheduled(cron = "0 0 1 * * ?")
    public void activerVente() {
        int count = articleAVendreDAO.activerVente();
        System.out.println(count + " ventes activées.");
    }

    // Toutes les nuits à 2h du matin
    @Scheduled(cron = "0 0 2 * * ?")
    public void cloturerVente() {
        int count = articleAVendreDAO.cloturerVente();
        System.out.println(count + " ventes clôturées.");
    }

	private boolean verifierArticleEstAutilisateur(int idArticle, String pseudo, BusinessException be) {
		int count = this.articleAVendreDAO.trouverProprietaireArticle(idArticle, pseudo);
		if (count < 1) {
			be.add("L'article ne vous appartient pas");
			return false;
		}
		return true;
	}
///--------- Voir si pas de conflit bigint int bdd---
	@Override
	public ArticleAVendre getById(long idArticle) {
		return this.articleAVendreDAO.getByID(idArticle);
	}

	@Override
	public List<ArticleAVendre> rechercherArticlesEnCours(String nomRecherche, int categorieRecherche) {
		return articleAVendreDAO.rechercherArticlesEnCours(nomRecherche, categorieRecherche);
	}

	@Override
	public List<ArticleAVendre> findArticlesEnCoursByVendeur(String pseudo) {
		return articleAVendreDAO.findArticlesEnCoursByVendeur(pseudo);
	}

	@Override
	public List<ArticleAVendre> findAllByUtilisateur(String pseudo) {
		return articleAVendreDAO.findAllByUtilisateur(pseudo);
	}

}
