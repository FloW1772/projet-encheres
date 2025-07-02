package fr.eni.encheres.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.eni.encheres.bll.ArticleAVendreService;
import fr.eni.encheres.bll.UtilisateurService;
import fr.eni.encheres.bo.ArticleAVendre;

@Controller
@RequestMapping("/")
public class ArticleAVendreController {

	ArticleAVendreService articleAVendreService;

	UtilisateurService utilisateurService;

	public ArticleAVendreController(ArticleAVendreService articleAVendreService,
			UtilisateurService utilisateurService) {
		this.articleAVendreService = articleAVendreService;
		this.utilisateurService = utilisateurService;
	}

	@GetMapping
	public String afficherArticleAVendre(Model model, Principal principal) {
	
	List<ArticleAVendre> articleAVendre = articleAVendreService.getArticlesAVendreEnCours();
	model.addAttribute("articleAVendre", articleAVendre);
	
	String nomRecherche = null;
	model.addAttribute("nomRecherche", nomRecherche);
	
	int categorieRecherche = 0;
	model.addAttribute("categorieRecherche", categorieRecherche);
	
	if(principal != null) {
		int casUtilisationFiltres = 0;
		model.addAttribute("casUtilisationFiltres", casUtilisationFiltres);
	}
	return "index";
}
/*	@PostMapping("/")
	public String afficherArticleAVendre(String nomRecherche, int categorieRecherche, int casUtilisationFiltres, Model model, Principal principal) {
		
	}*/
	
}


















