package fr.eni.encheres.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
			CategorieService categorieService, UtilisateurService utilisateurService) {
		this.enchereService = enchereService;
		this.articleAVendreService = articleAVendreService;
		this.categorieService = categorieService;
		this.utilisateurService = utilisateurService;
	}

	@GetMapping("/encheres")
	public String afficherListeEncheres(@RequestParam(name = "nomArticle", required = false) String nomRecherche,
			@RequestParam(name = "categorie", required = false, defaultValue = "0") int categorieRecherche,
			@RequestParam(name = "achatEncheresOuvertes", required = false, defaultValue = "false") boolean achatEncheresOuvertes,
			@RequestParam(name = "achatMesEncheres", required = false, defaultValue = "false") boolean achatMesEncheres,
			@RequestParam(name = "achatMesEncheresRemportees", required = false, defaultValue = "false") boolean achatMesEncheresRemportees,
			@RequestParam(name = "venteMesVentesEnCours", required = false, defaultValue = "false") boolean venteMesVentesEnCours,
			@RequestParam(name = "venteVentesNonDebutees", required = false, defaultValue = "false") boolean venteVentesNonDebutees,
			@RequestParam(name = "venteVentesTerminees", required = false, defaultValue = "false") boolean venteVentesTerminees,
			Principal principal, Model model) throws BusinessException {

		

		List<Categorie> categories = categorieService.getAllCategories();
		model.addAttribute("categories", categories);
		model.addAttribute("nomRecherche", nomRecherche);
		model.addAttribute("categorieRecherche", categorieRecherche);
		model.addAttribute("achatEncheresOuvertes", achatEncheresOuvertes);
		model.addAttribute("achatMesEncheres", achatMesEncheres);
		model.addAttribute("achatMesEncheresRemportees", achatMesEncheresRemportees);
		model.addAttribute("venteMesVentesEnCours", venteMesVentesEnCours);
		model.addAttribute("venteVentesNonDebutees", venteVentesNonDebutees);
		model.addAttribute("venteVentesTerminees", venteVentesTerminees);

		

		List<Enchere> resultats;

		if ((nomRecherche != null && !nomRecherche.isBlank()) || categorieRecherche > 0) {

			List<ArticleAVendre> articlesEnCours = articleAVendreService.rechercherArticlesEnCours(nomRecherche,
					categorieRecherche);

			// Convertir articles en enchères
			resultats = articlesEnCours.stream().map(article -> {
				Enchere e = new Enchere();
				e.setArticle(article);
				return e;
			}).collect(Collectors.toList());
		}

		else if (principal != null && (achatEncheresOuvertes || achatMesEncheres || achatMesEncheresRemportees
				|| venteMesVentesEnCours || venteVentesNonDebutees || venteVentesTerminees)) {

			String pseudo = principal.getName();

			String type = null;
			boolean encheresOuvertes = false, mesEncheres = false, mesEncheresRemportees = false;
			boolean ventesEnCours = false, ventesNonDebutees = false, ventesTerminees = false;

			if (achatEncheresOuvertes) {
				type = "achats";
				encheresOuvertes = true;
			} else if (achatMesEncheres) {
				type = "achats";
				mesEncheres = true;
			} else if (achatMesEncheresRemportees) {
				type = "achats";
				mesEncheresRemportees = true;
			} else if (venteMesVentesEnCours) {
				type = "ventes";
				ventesEnCours = true;
			} else if (venteVentesNonDebutees) {
				type = "ventes";
				ventesNonDebutees = true;
			} else if (venteVentesTerminees) {
				type = "ventes";
				ventesTerminees = true;
			}

			resultats = enchereService.filtrerEncheres(type, encheresOuvertes, mesEncheres, mesEncheresRemportees,
					ventesEnCours, ventesNonDebutees, ventesTerminees, pseudo);
		}

		else {
			List<ArticleAVendre> articlesEnCours = articleAVendreService.getArticlesAVendreEnCours();

			resultats = articlesEnCours.stream().map(article -> {
				Enchere enchere = new Enchere();
				enchere.setArticle(article);
				return enchere;
			}).collect(Collectors.toList());
		}

		/// corriger les adresse!!!
		for (Enchere enchere : resultats) {
			ArticleAVendre article = enchere.getArticle();
			if (article != null && article.getAdresseRetrait() == null && article.getVendeur() != null) {
				article.setAdresseRetrait(article.getVendeur().getAdresse());
			}
		}

		model.addAttribute("encheres", resultats);
		
		if (principal != null) {
		    Utilisateur utilisateurConnecte = utilisateurService.selectByLogin(principal.getName());
		    model.addAttribute("utilisateurConnecte", utilisateurConnecte);
		}
		
		
		String typeFiltre = (achatEncheresOuvertes || achatMesEncheres || achatMesEncheresRemportees) ? "achat" : "vente";
		model.addAttribute("typeFiltre", typeFiltre);
		
		return "view-liste-encheres";
	}

	@GetMapping("/articles/{id}")
	public String afficherDetailArticle(@PathVariable("id") long idArticle, Model model, Principal principal)
	        throws BusinessException {

	 
	    if (principal == null) {
	        return "redirect:/login";
	    }

	    ArticleAVendre article = articleAVendreService.getById(idArticle);
	       
	    if (article.getAdresseRetrait() == null && article.getVendeur() != null) {
	        Utilisateur vendeurComplet = utilisateurService.selectByLogin(article.getVendeur().getPseudo());
	        if (vendeurComplet != null && vendeurComplet.getAdresse() != null) {
	            article.setAdresseRetrait(vendeurComplet.getAdresse());
	        }
	    }
	    
	    model.addAttribute("article", article);
	    

	    Enchere meilleureEnchere = enchereService.selectBestEnchereByArticle(idArticle);
	    model.addAttribute("meilleureEnchere", meilleureEnchere);

	    Utilisateur utilisateurConnecte = null;
	    if (principal != null) {
	        String pseudo = principal.getName();
	        utilisateurConnecte = utilisateurService.selectByLogin(pseudo);
	    }
	    model.addAttribute("utilisateurConnecte", utilisateurConnecte);

	    String messageEnchere = null;
	    LocalDateTime now = LocalDateTime.now();
	    if(article.getDateFinEncheres().isBefore(now)) {
	    if (meilleureEnchere != null && utilisateurConnecte != null) {
	        if (utilisateurConnecte.getIdUtilisateur() == meilleureEnchere.getEncherisseur().getIdUtilisateur()) {
	            messageEnchere = utilisateurConnecte.getPseudo() + ", vous avez importé la vente";
	        } else if (utilisateurConnecte.getIdUtilisateur() == article.getVendeur().getIdUtilisateur()) {
	            messageEnchere = meilleureEnchere.getEncherisseur().getPseudo() + " a remporté la vente ";
	        }
	    }
	    model.addAttribute("messageEnchere", messageEnchere);
	    }
	    return "view-article-detail";
	}
	


	@PostMapping("/encherir")
	public String encherir(@RequestParam("idArticle") long idArticle, @RequestParam("montant") int montant,
			RedirectAttributes redirectAttributes, Model model, Principal principal) throws BusinessException {

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

			// Bloque si l utilisateur est le vendeur
		if (article.getVendeur().getIdUtilisateur() == utilisateurConnecte.getIdUtilisateur()) {
	        redirectAttributes.addFlashAttribute("messageErreur", "Vous ne pouvez pas enchérir sur votre propre article.");
	        return "redirect:/articles/" + idArticle;
	    }
		
		
		try {
			enchereService.encherir(idArticle, utilisateurConnecte.getIdUtilisateur(), montant);
			redirectAttributes.addFlashAttribute("messageSucces", "Votre enchère a bien été enregistrée.");
			return "redirect:/articles/" + idArticle;
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