package fr.eni.encheres.bll;

import java.util.List;

import org.springframework.stereotype.Service;

import fr.eni.encheres.bo.Enchere;
import fr.eni.encheres.dal.EnchereDAO;

@Service
public class EnchereServiceImpl implements EnchereService {

    private final EnchereDAO enchereDAO;

    public EnchereServiceImpl(EnchereDAO enchereDAO) {
        this.enchereDAO = enchereDAO;
    }

    @Override
    public void create(Enchere enchere) {
        enchereDAO.create(enchere);
    }

    @Override
    public Enchere readById(long idEnchere) {
        return enchereDAO.readById(idEnchere);
    }

    @Override
    public List<Enchere> readAll() {
        return enchereDAO.readAll();
    }

    @Override
    public void update(Enchere enchere) {
        enchereDAO.update(enchere);
    }

    @Override
    public void delete(long idEnchere) {
        enchereDAO.delete(idEnchere);
    }

    @Override
    public Enchere selectBestEnchereByArticle(long idArticle) {
        return enchereDAO.selectBestEnchereByArticle(idArticle);
    }

    @Override
    public List<Enchere> selectEncheresByUtilisateur(long idUtilisateur) {
        return enchereDAO.selectEncheresByUtilisateur(idUtilisateur);
    }

    @Override
    public List<Enchere> selectEncheresByArticle(long idArticle) {
        return enchereDAO.selectEncheresByArticle(idArticle);
    }

    @Override
    public void deleteByArticleId(long idArticle) {
        enchereDAO.deleteByArticleId(idArticle);
    }
}