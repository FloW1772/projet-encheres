package fr.eni.encheres.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.eni.encheres.bll.EnchereService;
import fr.eni.encheres.bo.ArticleAVendre;
import fr.eni.encheres.bo.Enchere;
import fr.eni.encheres.dal.ArticleAVendreDAO;
import fr.eni.encheres.exception.BusinessException;

@Controller
public class EnchereController {

	private final EnchereService enchereService;
	private final ArticleAVendreDAO articleAVendreDAO;

	public EnchereController(EnchereService enchereService, ArticleAVendreDAO articleAVendreDAO) {
		this.enchereService = enchereService;
		this.articleAVendreDAO = articleAVendreDAO;
	}

	@GetMapping("/encheres")
	public String afficherToutesLesEncheres(Model model) {
		List<Enchere> encheres = enchereService.readAll();
		model.addAttribute("encheres", encheres);
		return "view-liste-encheres";
	}

	@PostMapping("/encherir")
	public String encherir(@RequestParam("idArticle") long idArticle, @RequestParam("idUtilisateur") long idUtilisateur,
			@RequestParam("montant") int montant, Model model) {
		try {
			// On appelle la méthode encherir du service
			enchereService.encherir(idArticle, idUtilisateur, montant);

			// Si tout est ok on redirige vers la page de l'article
			return "redirect:/article/" + idArticle;

		} catch (BusinessException e) {

			model.addAttribute("messagesErreur", e.getMessages());

			ArticleAVendre article = articleAVendreDAO.getByID(idArticle);
			model.addAttribute("article", article);

			return "view-article-detail"; // Provisoire car vue non créée
		}
	}
}