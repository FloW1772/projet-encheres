package fr.eni.projet.enchere.controller;

import org.springframework.stereotype.Controller;

import fr.eni.projet.enchere.bll.UtilisateurService;
import fr.eni.projet.enchere.bll.ArticleAVendreService;

@Controller
public class ArticleAVendreController {

	ArticleAVendreService articleAVendreService;

	UtilisateurService utilisateurService;

	public ArticleAVendreController(ArticleAVendreService articleAVendreService,
			UtilisateurService utilisateurService) {
		this.articleAVendreService = articleAVendreService;
		this.utilisateurService = utilisateurService;
	}

	
	
	
	
	
	
	
}
