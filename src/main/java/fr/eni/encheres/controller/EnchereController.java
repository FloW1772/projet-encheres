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
import fr.eni.encheres.dal.ArticleAVendreDAO;
import fr.eni.encheres.dal.CategorieDAO;
import fr.eni.encheres.exception.BusinessException;

import jakarta.servlet.http.HttpSession;


@SessionAttributes({ "utilisateurSession", "utilisateurConnecte" })
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

	// Affichage de la page détail d’un article
	/// utilisation de HttpSession temporaires ;( a voir pour spring Security

	@GetMapping("/article/{id}")
	public String afficherDetailArticle(@PathVariable("id") long idArticle, Model model, HttpSession session) {
	    Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateurConnecte");

	    if (utilisateurConnecte == null) {
	        // utilisateur non connecté, redirection vers login
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
	                      HttpSession session) {

	    Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateurConnecte");
	    if (utilisateurConnecte == null) {
	        redirectAttributes.addFlashAttribute("messageErreur", "Vous devez être connecté pour enchérir.");
	        return "redirect:/login";
	    }

	    ArticleAVendre article = articleAVendreService.getById(idArticle);
	    if (article == null) {
	        redirectAttributes.addFlashAttribute("messageErreur", "Article introuvable.");
	        return "redirect:/encheres"; 
	    }

	    if (article.getVendeur().getIdUtilisateur() == utilisateurConnecte.getIdUtilisateur()) {
	        redirectAttributes.addFlashAttribute("messageErreur", "Vous ne pouvez pas enchérir sur votre propre article");
	        return "redirect:/article/" + idArticle;
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


////-------------- Activer pour la MEP -----------
	
	/*@PostMapping("/encherir")
	public String encherir(
	        @RequestParam("idArticle") long idArticle,
	        @RequestParam("montant") int montant,
	        Principal principal,
	        Model model) {

	    String pseudo = principal.getName();
	    Utilisateur utilisateur = utilisateurService.selectByLogin(pseudo);
	    long idUtilisateur = utilisateur.getIdUtilisateur();

	    try {
	        enchereService.encherir(idArticle, idUtilisateur, montant);
	        return "redirect:/article/" + idArticle;
	    } catch (BusinessException e) {
	        model.addAttribute("messagesErreur", e.getMessages());
	        return "detailVente";
	    }
	}*/

	
	
	//--------------
	
	
	
	
	// ----------- Faire test-login ça va dirriger vers le site enchere a desactiver
	// après la MEP
	@GetMapping("/test-login")
	public String testLogin(HttpSession session) {
		Utilisateur utilisateur = new Utilisateur();
		utilisateur.setIdUtilisateur(123L);
		utilisateur.setPseudo("testUser");
		session.setAttribute("utilisateurConnecte", utilisateur);
		return "redirect:/encheres";
	}
	
	@GetMapping("/")
	public String redirectRoot() {
	    return "redirect:/encheres";
	}
	
	@ModelAttribute("utilisateurSession")
	public List<Utilisateur> chargerUtilisateursEnSession() {
		System.out.println("Appel de la méthode chargerCoursEnSession");
		return utilisateurService.selectAll();
	}
	

}