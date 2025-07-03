package fr.eni.encheres.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.eni.encheres.bll.EnchereService;
import fr.eni.encheres.bo.ArticleAVendre;
import fr.eni.encheres.bo.Categorie;
import fr.eni.encheres.bo.Enchere;
import fr.eni.encheres.dal.ArticleAVendreDAO;
import fr.eni.encheres.dal.CategorieDAO;
import fr.eni.encheres.exception.BusinessException;

@Controller
public class EnchereController {

    private final EnchereService enchereService;
    private final ArticleAVendreDAO articleAVendreDAO;
    private final CategorieDAO categorieDAO;

    public EnchereController(EnchereService enchereService, ArticleAVendreDAO articleAVendreDAO, CategorieDAO categorieDAO) {
        this.enchereService = enchereService;
        this.articleAVendreDAO = articleAVendreDAO;
        this.categorieDAO = categorieDAO;
    }

    // Affichage de la liste avec filtres
    @GetMapping("/encheres")
    public String afficherListeEncheres(
        @RequestParam(name = "nomArticle", required = false) String nomRecherche,
        @RequestParam(name = "categorie", required = false, defaultValue = "0") int categorieRecherche,
        Model model
    ) {
        List<Categorie> categories = categorieDAO.findAll();
        List<Enchere> encheres = enchereService.rechercherEncheres(nomRecherche, categorieRecherche);

        model.addAttribute("categories", categories);
        model.addAttribute("encheres", encheres);
        model.addAttribute("nomRecherche", nomRecherche);
        model.addAttribute("categorieRecherche", categorieRecherche);

        return "view-liste-encheres";
    }

    //  Affichage de la page détail d’un article
    @GetMapping("/article/{id}")
    public String afficherDetailArticle(@PathVariable("id") long idArticle, Model model) {
        ArticleAVendre article = articleAVendreDAO.getByID(idArticle);
        Enchere meilleureEnchere = enchereService.selectBestEnchereByArticle(idArticle);

        model.addAttribute("article", article);
        model.addAttribute("meilleureEnchere", meilleureEnchere);

        return "view-article-detail";
    }

    //  Traitement de l’enchère
    @PostMapping("/encherir")
    public String encherir(
        @RequestParam("idArticle") long idArticle,
        @RequestParam("idUtilisateur") long idUtilisateur,
        @RequestParam("montant") int montant,
        Model model
    ) {
        try {
            enchereService.encherir(idArticle, idUtilisateur, montant);
            return "redirect:/article/" + idArticle;
        } catch (BusinessException e) {
            model.addAttribute("messagesErreur", e.getMessages());
            ArticleAVendre article = articleAVendreDAO.getByID(idArticle);
            Enchere meilleureEnchere = enchereService.selectBestEnchereByArticle(idArticle);

            model.addAttribute("article", article);
            model.addAttribute("meilleureEnchere", meilleureEnchere);
            return "view-article-detail";
        }
    }
}