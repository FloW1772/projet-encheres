package fr.eni.encheres.controller;

import org.springframework.stereotype.Controller;

import fr.eni.encheres.bll.ArticleAVendreService;
import fr.eni.projet.enchere.bll.UtilisateurService;

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
