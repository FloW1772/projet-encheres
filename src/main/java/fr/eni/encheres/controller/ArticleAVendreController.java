package fr.eni.encheres.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import fr.eni.encheres.bll.ArticleAVendreService;
import fr.eni.encheres.bll.CategorieService;
import fr.eni.encheres.bll.UtilisateurService;
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

    @Autowired
    public ArticleAVendreController(ArticleAVendreService articleAVendreService,
                                    UtilisateurService utilisateurService,
                                    CategorieService categorieService) {
        this.articleAVendreService = articleAVendreService;
        this.utilisateurService = utilisateurService;
        this.categorieService = categorieService;
    }

    @GetMapping("/creation")
    public String afficherFormulaireVente(Model model, Principal principal) {
        model.addAttribute("articleAVendre", new ArticleAVendre());
        model.addAttribute("categories", categorieService.getAllCategories());
        model.addAttribute("modeModif", false);
        return "view-vente-article";
    }

    @PostMapping("/creation")
    public String traiterVente(@Valid @ModelAttribute("articleAVendre") ArticleAVendre articleAVendre,
                               BindingResult bindingResult,
                               Principal principal,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categorieService.getAllCategories());
            model.addAttribute("modeModif", false);
            return "view-vente-article";
        }

        try {
            Utilisateur utilisateur = utilisateurService.selectByLogin(principal.getName());
            articleAVendreService.mettreArticleEnVente(articleAVendre, utilisateur, articleAVendre.getCategorie().getIdCategorie());
        } catch (BusinessException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "view-vente-article";
        }
 
        return "redirect:/";
    }

    @GetMapping("/vente/annuler")
    public String annulerVente(@RequestParam("id") int id) {
        try {
            ArticleAVendre article = articleAVendreService.getById(id);
            articleAVendreService.annulerVente(article);
        } catch (BusinessException e) {
            // Tu peux logger ou afficher un message d’erreur si nécessaire
        }
        return "redirect:/";
    }

    @PostMapping("/rechercher")
    public String afficherArticlesFiltres(@RequestParam("nomRecherche") String nomRecherche,
                                          @RequestParam("categorieRecherche") int categorieRecherche,
                                          @RequestParam("casUtilisationFiltres") int casUtilisationFiltres,
                                          Model model,
                                          Principal principal) {

        List<ArticleAVendre> articlesAVendre = articleAVendreService.getArticlesAVendreAvecParamètres(
                nomRecherche, categorieRecherche, casUtilisationFiltres, principal);

        model.addAttribute("articlesAVendre", articlesAVendre);
        model.addAttribute("nomRecherche", nomRecherche);
        model.addAttribute("categorieRecherche", categorieRecherche);
        model.addAttribute("casUtilisationFiltres", casUtilisationFiltres);

        return "index";
    }
}
















