package fr.eni.encheres.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import fr.eni.encheres.bll.CategorieService;
import fr.eni.encheres.bo.Categorie;

@Controller
public class CategorieController {

	private final CategorieService categorieService;
	
	public CategorieController(CategorieService categorieService) {
		this.categorieService = categorieService;
	}
	

	
	@GetMapping("/categories")
	public String afficherListeCategories(Model model) {
		List<Categorie> categories = categorieService.getAllCategories();
		model.addAttribute("categories", categories);
		return "view-liste-categorie";
		
	}
	
	@GetMapping("/categories/ajout")
	public String afficherFormulaireAjoutCategorie(Model model) {
	    model.addAttribute("categorie", new Categorie());
	    return "view-ajout-categorie";
	}
	
	@PostMapping("/categories/ajout")
	public String ajouterCategorie(@ModelAttribute Categorie categorie) {
	    categorieService.ajouterCategorie(categorie); // cette méthode est à faire côté service
	    return "redirect:/categories"; // redirection vers la liste
	}
	
}
