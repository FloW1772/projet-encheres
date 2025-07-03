package fr.eni.encheres.bll;

import java.util.List;

import fr.eni.encheres.bo.Enchere;
import fr.eni.encheres.exception.BusinessException;

public interface EnchereService {

    void create(Enchere enchere);

    Enchere readById(long idEnchere);

    List<Enchere> readAll();

    void update(Enchere enchere);

    void delete(long idEnchere);

    Enchere selectBestEnchereByArticle(long idArticle);

    List<Enchere> selectEncheresByUtilisateur(long idUtilisateur);

    List<Enchere> selectEncheresByArticle(long idArticle);

    void deleteByArticleId(long idArticle);
    
    void encherir(long idArticle, long idUtilisateur, int Montant) throws BusinessException;
}