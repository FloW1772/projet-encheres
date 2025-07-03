package fr.eni.encheres.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.eni.encheres.bll.ArticleAVendreService;
import fr.eni.encheres.bll.CategorieService;
import fr.eni.encheres.bll.EnchereService;
import fr.eni.encheres.bo.ArticleAVendre;
import fr.eni.encheres.bo.Categorie;
import fr.eni.encheres.bo.Enchere;
import fr.eni.encheres.bo.Utilisateur;
import fr.eni.encheres.dal.ArticleAVendreDAO;
import fr.eni.encheres.dal.CategorieDAO;
import fr.eni.encheres.exception.BusinessException;
import jakarta.servlet.http.HttpSession;

@Controller
public class EnchereController {

    private final EnchereService enchereService;
    private final ArticleAVendreService articleAVendreService;
    private final CategorieService categorieService;

    public EnchereController(EnchereService enchereService, ArticleAVendreService articleAVendreService, CategorieService categorieService) {
        this.enchereService = enchereService;
        this.articleAVendreService = articleAVendreService;
        this.categorieService = categorieService;
    }

    @GetMapping("/encheres")
    public String afficherListeEncheres(
        @RequestParam(name = "nomArticle", required = false) String nomRecherche,
        @RequestParam(name = "categorie", required = false, defaultValue = "0") int categorieRecherche,
        Model model
    ) {
        List<Categorie> categories = categorieService.getAllCategories();
        List<ArticleAVendre> articlesEnCours;

        if ((nomRecherche == null || nomRecherche.isEmpty()) && categorieRecherche == 0) {
            // Sans filtre, afficher tous les articles en cours
            articlesEnCours = articleAVendreService.getArticlesAVendreEnCours();
        } else {
            // Avec filtre
            articlesEnCours = articleAVendreService.rechercherArticlesEnCours(nomRecherche, categorieRecherche);
        }

        model.addAttribute("categories", categories);
        model.addAttribute("articles", articlesEnCours);
        model.addAttribute("nomRecherche", nomRecherche);
        model.addAttribute("categorieRecherche", categorieRecherche);

        return "view-liste-encheres";
    }

    //  Affichage de la page détail d’un article
   /// utilisation de HttpSession temporaires ;( a voir pour spring Security

    @GetMapping("/article/{id}")
    public String afficherDetailArticle(
        @PathVariable("id") long idArticle,
        Model model,
        HttpSession session
    ) {
        ArticleAVendre article = articleAVendreService.getById(idArticle);
        Enchere meilleureEnchere = enchereService.selectBestEnchereByArticle(idArticle);

   
        Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateurConnecte");

        model.addAttribute("article", article);
        model.addAttribute("meilleureEnchere", meilleureEnchere);
        model.addAttribute("utilisateurConnecte", utilisateurConnecte); 

        return "view-article-detail";
    }


    @PostMapping("/encherir")
    public String encherir(
        @RequestParam("idArticle") long idArticle,
        @RequestParam("idUtilisateur") long idUtilisateur,
        @RequestParam("montant") int montant,
        Model model,
        HttpSession session
    ) {
        try {
            enchereService.encherir(idArticle, idUtilisateur, montant);
            return "redirect:/article/" + idArticle;
        } catch (BusinessException e) {
            model.addAttribute("messagesErreur", e.getMessages());

            ArticleAVendre article = articleAVendreService.getById(idArticle);
            Enchere meilleureEnchere = enchereService.selectBestEnchereByArticle(idArticle);

            Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateurConnecte");

            model.addAttribute("article", article);
            model.addAttribute("meilleureEnchere", meilleureEnchere);
            model.addAttribute("utilisateurConnecte", utilisateurConnecte);

            return "view-article-detail";
        }
    }

}