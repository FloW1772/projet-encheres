package fr.eni.encheres.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import fr.eni.encheres.bll.ArticleAVendreService;
import fr.eni.encheres.bll.CategorieService;
import fr.eni.encheres.bll.EnchereService;
import fr.eni.encheres.bll.UtilisateurService;
import fr.eni.encheres.bo.ArticleAVendre;
import fr.eni.encheres.bo.Categorie;
import fr.eni.encheres.bo.Enchere;
import fr.eni.encheres.bo.Utilisateur;
import fr.eni.encheres.exception.BusinessException;



@Controller
public class EnchereController {

	private final EnchereService enchereService;
	private final ArticleAVendreService articleAVendreService;
	private final CategorieService categorieService;
	private final UtilisateurService utilisateurService;

	public EnchereController(EnchereService enchereService, ArticleAVendreService articleAVendreService,
			CategorieService categorieService,UtilisateurService utilisateurService) {
		this.enchereService = enchereService;
		this.articleAVendreService = articleAVendreService;
		this.categorieService = categorieService;
		this.utilisateurService =utilisateurService;
	}

	@GetMapping("/encheres")
	public String afficherListeEncheres(@RequestParam(name = "nomArticle", required = false) String nomRecherche,
			@RequestParam(name = "categorie", required = false, defaultValue = "0") int categorieRecherche,
			Model model) {
		List<Categorie> categories = categorieService.getAllCategories();
		List<ArticleAVendre> articlesEnCours;

		if ((nomRecherche == null || nomRecherche.isEmpty()) && categorieRecherche == 0) {
			// Sans filtre, afficher tous les articles en cours
			articlesEnCours = articleAVendreService.getArticlesAVendreEnCours();
		} else {
			// Avec filtre
			articlesEnCours = articleAVendreService.rechercherArticlesEnCours(nomRecherche, categorieRecherche);
		}
		
		  for (ArticleAVendre article : articlesEnCours) {
		        if (article.getAdresseRetrait() == null && article.getVendeur() != null) {
		            article.setAdresseRetrait(article.getVendeur().getAdresse());
		        }
		    }
		model.addAttribute("categories", categories);
		model.addAttribute("articles", articlesEnCours);
		model.addAttribute("nomRecherche", nomRecherche);
		model.addAttribute("categorieRecherche", categorieRecherche);

		return "view-liste-encheres";
	}



	@GetMapping("/article/{id}")
	public String afficherDetailArticle(@PathVariable("id") long idArticle, Model model, Principal principal) throws BusinessException {
		String login = principal.getName();
		
		Utilisateur utilisateurConnecte = utilisateurService.selectByLogin(login);
		

	    if (utilisateurConnecte == null) {
	        return "redirect:/login";
	    }

	    ArticleAVendre article = articleAVendreService.getById(idArticle);
	    Enchere meilleureEnchere = enchereService.selectBestEnchereByArticle(idArticle);

	    model.addAttribute("article", article);
	    model.addAttribute("meilleureEnchere", meilleureEnchere);
	    model.addAttribute("utilisateurConnecte", utilisateurConnecte);

	    return "view-article-detail";
	}

	
	@PostMapping("/encherir")
	public String encherir(@RequestParam("idArticle") long idArticle,
	                      @RequestParam("montant") int montant,
	                      RedirectAttributes redirectAttributes,
	                      Model model,
	                      Principal principal) throws BusinessException {

		String login = principal.getName();
		
		Utilisateur utilisateurConnecte = utilisateurService.selectByLogin(login);
		
	    if (utilisateurConnecte == null) {
	        redirectAttributes.addFlashAttribute("messageErreur", "Vous devez être connecté pour enchérir.");
	        return "redirect:/login";
	    }

	    ArticleAVendre article = articleAVendreService.getById(idArticle);
	    if (article == null) {
	        redirectAttributes.addFlashAttribute("messageErreur", "Article introuvable.");
	        return "redirect:/encheres"; 
	    }

	    try {
	        enchereService.encherir(idArticle, utilisateurConnecte.getIdUtilisateur(), montant);
	        return "redirect:/article/" + idArticle;
	    } catch (BusinessException e) {
	        model.addAttribute("messagesErreur", e.getMessages());
	        model.addAttribute("article", article);

	        Enchere meilleureEnchere = enchereService.selectBestEnchereByArticle(idArticle);
	        model.addAttribute("meilleureEnchere", meilleureEnchere);
	        model.addAttribute("utilisateurConnecte", utilisateurConnecte);

	        return "view-article-detail";
	    }
	}

	
	
	@GetMapping("/")
	public String redirectRoot() {
	    return "redirect:/encheres";
	}

	

}