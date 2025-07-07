package fr.eni.encheres.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.eni.encheres.bll.AdresseService;
import fr.eni.encheres.bll.ArticleAVendreService;
import fr.eni.encheres.bll.CategorieService;
import fr.eni.encheres.bll.UtilisateurService;
import fr.eni.encheres.bo.Adresse;
import fr.eni.encheres.bo.ArticleAVendre;
import fr.eni.encheres.bo.Utilisateur;
import fr.eni.encheres.exception.BusinessException;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/vente")
public class ArticleAVendreController {

	private final ArticleAVendreService articleAVendreService;
	private final UtilisateurService utilisateurService;
	private final CategorieService categorieService;
	private final AdresseService adresseService;

	public ArticleAVendreController(ArticleAVendreService articleAVendreService, UtilisateurService utilisateurService,
			CategorieService categorieService, AdresseService adresseService) {
		this.articleAVendreService = articleAVendreService;
		this.utilisateurService = utilisateurService;
		this.categorieService = categorieService;
		this.adresseService = adresseService;
	}


	 @GetMapping("/creation")
	    public String afficherFormulaireVente(Model model, Principal principal) throws BusinessException {
	        Utilisateur utilisateur = getUtilisateurConnecte(principal);
	        if (utilisateur == null) {
	            return "redirect:/login";
	        }

	        ArticleAVendre article = new ArticleAVendre();
	        article.setVendeur(utilisateur);

	        Adresse adresseUtilisateur = adresseService.selectAllByUtilisateurId(utilisateur.getIdUtilisateur());
	        article.setAdresseRetrait(adresseUtilisateur != null ? adresseUtilisateur : new Adresse());

	        preparerModelVente(model, article, false);
	        return "view-vente-article";
	    }

	    //  Création de l'article
	    @PostMapping("/creation")
	    public String creationVente(@Valid @ModelAttribute("articleAVendre") ArticleAVendre articleAVendre,
	                                BindingResult bindingResult, Model model, Principal principal) throws BusinessException {
	        Utilisateur utilisateur = getUtilisateurConnecte(principal);
	        if (utilisateur == null) {
	            return "redirect:/login";
	        }

	        if (bindingResult.hasErrors()) {
	            preparerModelVente(model, articleAVendre, false);
	            return "view-vente-article";
	        }

	        try {
	            articleAVendreService.mettreArticleEnVente(articleAVendre, utilisateur,
	                    articleAVendre.getCategorie().getIdCategorie());
	        } catch (BusinessException e) {
	            model.addAttribute("errorMessage", e.getMessage());
	            preparerModelVente(model, articleAVendre, false);
	            return "view-vente-article";
	        }

	        return "redirect:/";
	    }

	    //  Annuler une vente
	    @GetMapping("/annuler")
	    public String annulerVente(@RequestParam("id") int id) {
	        try {
	            ArticleAVendre article = articleAVendreService.getById(id);
	            articleAVendreService.annulerVente(article);
	        } catch (BusinessException ignored) {
	        }
	        return "redirect:/";
	    }

	    //  Rechercher des articles
	  /*  @PostMapping("/rechercher")
	    public String afficherArticlesFiltres(@RequestParam("nomRecherche") String nomRecherche,
	                                          @RequestParam("categorieRecherche") int categorieRecherche,
	                                          @RequestParam("casUtilisationFiltres") int casUtilisationFiltres,
	                                          Model model, Principal principal) {

	        List<ArticleAVendre> articlesAVendre = articleAVendreService.getArticlesAVendreAvecParamètres(
	                nomRecherche, categorieRecherche, casUtilisationFiltres, principal
	        );

	        model.addAttribute("articlesAVendre", articlesAVendre);
	        model.addAttribute("nomRecherche", nomRecherche);
	        model.addAttribute("categorieRecherche", categorieRecherche);
	        model.addAttribute("casUtilisationFiltres", casUtilisationFiltres);

	        return "index";
	    }*/

	    //  Afficher les ventes de l'utilisateur
	    @GetMapping("/mes-ventes")
	    public String afficherMesVentes(Principal principal, Model model) throws BusinessException {
	        Utilisateur utilisateur = getUtilisateurConnecte(principal);
	        if (utilisateur == null) {
	            return "redirect:/login";
	        }

	        List<ArticleAVendre> articles = articleAVendreService.findArticlesEnCoursByVendeur(utilisateur.getPseudo());
	        model.addAttribute("articles", articles);
	        return "view-liste-articles-utilisateur";
	    }

	    //  Modifier un article
	    @GetMapping("/modifier/{id}")
	    public String modifierArticle(@PathVariable("id") long id, Principal principal, Model model,
	                                   RedirectAttributes redirectAttributes) throws BusinessException {
	        Utilisateur utilisateur = getUtilisateurConnecte(principal);
	        if (utilisateur == null) {
	            return "redirect:/login";
	        }

	        ArticleAVendre article = articleAVendreService.getById(id);
	        if (article == null || !article.getVendeur().getPseudo().equals(utilisateur.getPseudo())) {
	            redirectAttributes.addFlashAttribute("errorMessage", "Accès refusé à l'article.");
	            return "redirect:/vente/mes-ventes";
	        }

	        model.addAttribute("articleAVendre", article);
	        model.addAttribute("categories", categorieService.getAllCategories());
	        model.addAttribute("modeModif", true);

	        return "view-vente-article";
	    }

	    //  Supprimer un article
	    @GetMapping("/supprimer/{id}")
	    public String supprimerArticle(@PathVariable("id") long id, Principal principal,
	                                    RedirectAttributes redirectAttributes) throws BusinessException {
	        Utilisateur utilisateur = getUtilisateurConnecte(principal);
	        if (utilisateur == null) {
	            return "redirect:/login";
	        }

	        ArticleAVendre article = articleAVendreService.getById(id);
	        if (article == null || !article.getVendeur().getPseudo().equals(utilisateur.getPseudo())) {
	            redirectAttributes.addFlashAttribute("errorMessage", "Accès refusé à l'article.");
	            return "redirect:/vente/mes-ventes";
	        }

	        try {
	            articleAVendreService.annulerVente(article);
	            redirectAttributes.addFlashAttribute("successMessage", "Article supprimé avec succès.");
	        } catch (BusinessException be) {
	            redirectAttributes.addFlashAttribute("errorMessage", String.join(", ", be.getMessages()));
	        }

	        return "redirect:/vente/mes-ventes";
	    }

	    //  Méthode utilitaire pour préparer le modèle
	    private void preparerModelVente(Model model, ArticleAVendre article, boolean modeModif) {
	        if (article.getAdresseRetrait() == null) {
	            article.setAdresseRetrait(new Adresse());
	        }
	        model.addAttribute("articleAVendre", article);
	        model.addAttribute("categories", categorieService.getAllCategories());
	        model.addAttribute("modeModif", modeModif);
	    }

	    //  Méthode utilitaire pour récupérer l'utilisateur connecté
	    private Utilisateur getUtilisateurConnecte(Principal principal) throws BusinessException {
	        if (principal == null) return null;
	        return utilisateurService.selectByLogin(principal.getName());
	    }
=======
	// ----------Pout tester la creation passage a la mano de id utilisateur -------

	private void ajouterAttributsFormulaire(Model model, ArticleAVendre article, boolean modeModif) {
		model.addAttribute("articleAVendre", article);
		model.addAttribute("categories", categorieService.getAllCategories());
		model.addAttribute("modeModif", modeModif);
	}

	private void preparerModelVente(Model model, ArticleAVendre article, boolean modeModif) {
		if (article.getAdresseRetrait() == null) {
			article.setAdresseRetrait(new Adresse());
		}
		model.addAttribute("articleAVendre", article);
		model.addAttribute("categories", categorieService.getAllCategories());
		model.addAttribute("modeModif", modeModif);
	}

	// -------- A finaliser---------
	@GetMapping("/creation")
	public String afficherFormulaireVente(Model model, Principal principal) {
		String login = principal.getName();
		Utilisateur utilisateur;

		try {
			utilisateur = utilisateurService.selectByLogin(login);
		} catch (BusinessException e) {
			e.printStackTrace();
			return "erreur";
		}

		ArticleAVendre article = new ArticleAVendre();
		article.setVendeur(utilisateur);

		Adresse adresseUtilisateur = adresseService.selectAllByUtilisateurId(utilisateur.getIdUtilisateur());
		if (adresseUtilisateur != null) {
			article.setAdresseRetrait(adresseUtilisateur);
		} else {
			article.setAdresseRetrait(new Adresse());
		}

		ajouterAttributsFormulaire(model, article, false);
		return "view-vente-article";
	}


	@PostMapping("/creation")
	public String traiterVente(@Valid @ModelAttribute("articleAVendre") ArticleAVendre articleAVendre,
			BindingResult bindingResult, Principal principal, Model model) {
		if (bindingResult.hasErrors()) {
			if (articleAVendre.getAdresseRetrait() == null) {
				articleAVendre.setAdresseRetrait(new Adresse());
			}
			ajouterAttributsFormulaire(model, articleAVendre, false);
			return "view-vente-article";
		}

		try {
			Utilisateur utilisateur = utilisateurService.selectByLogin(principal.getName());
			articleAVendre.setVendeur(utilisateur);
			articleAVendreService.mettreArticleEnVente(articleAVendre, utilisateur,
					articleAVendre.getCategorie().getIdCategorie());
		} catch (BusinessException e) {
			model.addAttribute("errorMessage", e.getMessage());
			if (articleAVendre.getAdresseRetrait() == null) {
				articleAVendre.setAdresseRetrait(new Adresse());
			}
			ajouterAttributsFormulaire(model, articleAVendre, false);
			return "view-vente-article";
		}

		return "redirect:/";
	}

	@GetMapping("/annuler")
	public String annulerVente(@RequestParam("id") int id) {
		try {
			ArticleAVendre article = articleAVendreService.getById(id);
			articleAVendreService.annulerVente(article);
		} catch (BusinessException e) {
		}
		return "redirect:/";
	}

	@PostMapping("/rechercher")
	public String afficherArticlesFiltres(@RequestParam("nomRecherche") String nomRecherche,
			@RequestParam("categorieRecherche") int categorieRecherche,
			@RequestParam("casUtilisationFiltres") int casUtilisationFiltres, Model model, Principal principal) {

		List<ArticleAVendre> articlesAVendre = articleAVendreService.getArticlesAVendreAvecParamètres(nomRecherche,
				categorieRecherche, casUtilisationFiltres, principal);

		model.addAttribute("articlesAVendre", articlesAVendre);
		model.addAttribute("nomRecherche", nomRecherche);
		model.addAttribute("categorieRecherche", categorieRecherche);
		model.addAttribute("casUtilisationFiltres", casUtilisationFiltres);

		return "index";
	}

	@GetMapping("/mes-ventes")
	public String afficherMesVentes(HttpSession session, Model model) {
		Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateurConnecte");
		List<ArticleAVendre> articles;

		if (utilisateurConnecte == null) {
			return "redirect:/login";
		} else {
			articles = articleAVendreService.findArticlesEnCoursByVendeur(utilisateurConnecte.getPseudo());
		}

		model.addAttribute("articles", articles);

		return "view-liste-articles-utilisateur";
	}

	@GetMapping("/modifier/{id}")
	public String modifierArticle(@PathVariable("id") long id, HttpSession session, Model model,
			RedirectAttributes redirectAttributes) {
		Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateurConnecte");
		if (utilisateur == null) {
			return "redirect:/login";
		}

		ArticleAVendre article = articleAVendreService.getById(id);
		if (article == null || !article.getVendeur().getPseudo().equals(utilisateur.getPseudo())) {
			redirectAttributes.addFlashAttribute("errorMessage", "Accès refusé à l'article.");
			return "redirect:/vente/mes-ventes";
		}

		model.addAttribute("articleAVendre", article);
		model.addAttribute("categories", categorieService.getAllCategories());
		model.addAttribute("modeModif", true);

		return "view-vente-article";
	}

	@GetMapping("/supprimer/{id}")
	public String supprimerArticle(@PathVariable("id") long id, HttpSession session,
			RedirectAttributes redirectAttributes) {
		Utilisateur utilisateur = (Utilisateur) session.getAttribute("utilisateurConnecte");
		if (utilisateur == null) {
			return "redirect:/login";
		}

		ArticleAVendre article = articleAVendreService.getById(id);
		if (article == null || !article.getVendeur().getPseudo().equals(utilisateur.getPseudo())) {
			redirectAttributes.addFlashAttribute("errorMessage", "Accès refusé à l'article.");
			return "redirect:/vente/mes-ventes";
		}

		try {
			articleAVendreService.annulerVente(article);
			redirectAttributes.addFlashAttribute("successMessage", "Article supprimé avec succès.");
		} catch (BusinessException be) {
			redirectAttributes.addFlashAttribute("errorMessage", String.join(", ", be.getMessages()));
		}

		return "redirect:/vente/mes-ventes";
	}


}
