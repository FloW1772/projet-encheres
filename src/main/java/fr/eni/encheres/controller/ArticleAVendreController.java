package fr.eni.encheres.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import fr.eni.enchere.bll.UtilisateurService;
import fr.eni.encheres.bll.ArticleAVendreService;

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

	

	
	
	
	
	
	
	
}
