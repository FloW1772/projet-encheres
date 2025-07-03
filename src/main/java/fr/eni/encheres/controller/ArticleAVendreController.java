package fr.eni.encheres.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.eni.encheres.bll.ArticleAVendreService;
import fr.eni.encheres.bll.CategorieService;
import fr.eni.encheres.bll.UtilisateurService;
import fr.eni.encheres.bo.ArticleAVendre;
import fr.eni.encheres.bo.Categorie;

@Controller
//@RequestMapping("/vente")
public class ArticleAVendreController {

	ArticleAVendreService articleAVendreService;

	UtilisateurService utilisateurService;
	CategorieService categorieService;
	public ArticleAVendreController(ArticleAVendreService articleAVendreService,
			UtilisateurService utilisateurService, CategorieService categorieService) {
		this.articleAVendreService = articleAVendreService;
		this.utilisateurService = utilisateurService;
		this.categorieService = categorieService;
	}

	/**
	 * Affiche les articles à vendre en cours.
	 *
	 * @param model     Le modèle pour la vue.
	 * @param principal Les informations de l'utilisateur connecté.
	 * @return La vue index.
	 */

	@GetMapping("/vente/creation")
	public String afficherArticleAVendre(Model model, Principal principal) {
	
	ArticleAVendre articleAVendre = new ArticleAVendre();
	articleAVendre.getCategorie();
	model.addAttribute("articleAVendre", articleAVendre);
	model.addAttribute("categories",categorieService.getAllCategories() );
	
	// Ajout au model ma variable "nomRecherche" qui contiendra la chaine de
			// caractère a retrouver dans le nom des articles
	
	/*int categorieRecherche = 0;
	model.addAttribute("categorieRecherche", categorieRecherche);
	// Ajout de la condition "est connecté"
	if(principal != null) {
		// Ajout des parametres utiles aux filtres si l'utilisateurs est connecté et non
					// Admin.
					// Parametre pour les input Select
		int casUtilisationFiltres = 0;
		model.addAttribute("casUtilisationFiltres", casUtilisationFiltres);
	}*/
	return "view-vente-article";
}
	/**
	 * Affiche les articles à vendre en fonction des paramètres de recherche.
	 *
	 * @param nomRecherche          Le nom de l'article recherché.
	 * @param categorieRecherche    L'identifiant de la catégorie recherchée.
	 * @param casUtilisationFiltres Les filtres d'utilisation.
	 * @param model                 Le modèle pour la vue.
	 * @param principal             Les informations de l'utilisateur connecté.
	 * @return La vue index.
	 */
	@PostMapping("/rechercher")
	public String afficherArticleAVendre(@RequestParam(value = "nomRecherche") String nomRecherche,
			@RequestParam(value = "categorieRecherche") int categorieRecherche,
			@RequestParam(value = "casUtilisationFiltres") int casUtilisationFiltres, Model model,
			Principal principal) {
		List<ArticleAVendre> articlesAVendre = articleAVendreService.getArticlesAVendreAvecParamètres(nomRecherche,
				categorieRecherche, casUtilisationFiltres, principal);
		model.addAttribute("articlesAVendre", articlesAVendre);
		model.addAttribute("nomRecherche", nomRecherche);
		model.addAttribute("categorieRecherche", categorieRecherche);

		if (principal != null) {
			// Ajout des parametres utiles aux filtres si l'utilisateurs est connecté et non
			// Admin.
			// Parametre pour les input select
			model.addAttribute("casUtilisationFiltres", casUtilisationFiltres);
		}
		return "index";

	}
	/**
	 * Affiche le formulaire pour mettre en vente un nouvel article.
	 *
	 * @param model     Le modèle pour la vue.
	 * @param principal Les informations de l'utilisateur connecté.
	 * @return La vue du formulaire de vente d'article.
	 */





	/**
	 * Traite la mise en vente d'un nouvel article.
	 *
	 * @param articleAVendre L'article à vendre.
	 * @param bindingResult  Les résultats de la validation.
	 * @param principal      Les informations de l'utilisateur connecté.
	 * @param model          Le modèle pour la vue.
	 * @return La vue à afficher.
	 */














}














