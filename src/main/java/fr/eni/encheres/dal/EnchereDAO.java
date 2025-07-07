package fr.eni.encheres.dal;

import java.util.List;
import fr.eni.encheres.bo.Enchere;

public interface EnchereDAO {

    void create(Enchere enchere);
    
    void update(Enchere enchere);

    void delete(long idEnchere);

    void deleteByArticleId(long idArticle);

    Enchere readById(long idEnchere);
    
    Enchere selectBestEnchereByArticle(long idArticle);
    
    List<Enchere> readAll();

    List<Enchere> selectEncheresByUtilisateur(long idUtilisateur);

    List<Enchere> selectEncheresByArticle(long idArticle);
    
    List<Enchere> rechercherEncheres(String nomArticle, int idCategorie);
    
    List<Enchere> selectMesEncheres(String pseudo);
    
    List<Enchere> getEncheresOuvertes();

    List<Enchere> selectMesEncheresRemportees(String pseudo);

    List<Enchere> selectMesVentesEnCours(String pseudo);

    List<Enchere> selectMesVentesNonDebutees(String pseudo);

    List<Enchere> selectMesVentesTerminees(String pseudo);
   
}
