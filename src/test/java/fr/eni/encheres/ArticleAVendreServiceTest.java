package fr.eni.encheres;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import fr.eni.encheres.bll.ArticleAVendreService;
import fr.eni.encheres.bo.ArticleAVendre;
import fr.eni.encheres.bo.EtatVente;
import fr.eni.encheres.dal.ArticleAVendreDAO;

@SpringBootTest
public class ArticleAVendreServiceTest {

    @Autowired
    private ArticleAVendreService service;

    @Autowired
    private ArticleAVendreDAO dao;

    @Test
    public void testActiverVente() {
        List<ArticleAVendre> articlesAvant = dao.findAllByUtilisateur("mariana28").stream()
            .filter(a -> a.getDateDebutEncheres().toLocalDate().isEqual(LocalDate.now()))
            .filter(a -> a.getEtatVente() == EtatVente.NON_DEMARREE)
            .toList();

        service.activerVente();

        List<ArticleAVendre> articlesApres = dao.findAllByUtilisateur("mariana28").stream()
            .filter(a -> a.getDateDebutEncheres().toLocalDate().isEqual(LocalDate.now()))
            .filter(a -> a.getEtatVente() == EtatVente.EN_COURS)
            .toList();

        assertThat(articlesApres.size()).isEqualTo(articlesAvant.size());
    }

    @Test
    public void testCloturerVente() {
        List<ArticleAVendre> articlesAvant = dao.findAllByUtilisateur("mariana28");
        long aCloturer = articlesAvant.stream()
            .filter(a -> a.getDateFinEncheres().toLocalDate().isEqual(LocalDate.now()))
            .filter(a -> a.getEtatVente() == EtatVente.EN_COURS)
            .count();

        service.cloturerVente();

        List<ArticleAVendre> articlesApres = dao.findAllByUtilisateur("mariana28");
        long clotures = articlesApres.stream()
            .filter(a -> a.getDateFinEncheres().toLocalDate().isEqual(LocalDate.now()))
            .filter(a -> a.getEtatVente() == EtatVente.TERMINEE)
            .count();

        System.out.println("Articles à clôturer trouvés : " + aCloturer);
        System.out.println("Articles clôturés après appel : " + clotures);

        assertThat(clotures).isEqualTo(aCloturer);
    }
}
