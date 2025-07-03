package fr.eni.encheres.dal.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import fr.eni.encheres.bo.ArticleAVendre;
import fr.eni.encheres.bo.Enchere;
import fr.eni.encheres.bo.Utilisateur;
import fr.eni.encheres.dal.EnchereDAO;

@Repository
public class EnchereDAOImpl implements EnchereDAO {

    private final String INSERT = "INSERT INTO Enchere(dateEnchere, montant, idUtilisateur, idArticle) VALUES (:dateEnchere, :montant, :idUtilisateur, :idArticle)";
    private final String SELECT_BY_ID = "SELECT * FROM Enchere WHERE idEnchere = :idEnchere";
    private final String SELECT_ALL = "SELECT * FROM Enchere";
    private final String UPDATE = "UPDATE Enchere SET dateEnchere = :dateEnchere, montant = :montant, idUtilisateur = :idUtilisateur, idArticle = :idArticle WHERE idEnchere = :idEnchere";
    private final String DELETE = "DELETE FROM Enchere WHERE idEnchere = :idEnchere";
    private final String SELECT_BEST_BY_ARTICLE = "SELECT TOP 1 * FROM Enchere WHERE idArticle = :idArticle ORDER BY montant DESC";
    private final String SELECT_BY_UTILISATEUR = "SELECT * FROM Enchere WHERE idUtilisateur = :idUtilisateur";
    private final String SELECT_BY_ARTICLE = "SELECT * FROM Enchere WHERE idArticle = :idArticle";
    private final String DELETE_BY_ARTICLE = "DELETE FROM Enchere WHERE idArticle = :idArticle";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public EnchereDAOImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public void create(Enchere enchere) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("dateEnchere", enchere.getDateEnchere());
        params.addValue("montant", enchere.getMontant());
        params.addValue("idUtilisateur", enchere.getEncherisseur().getIdUtilisateur());
        params.addValue("idArticle", enchere.getArticle().getIdArticle());

        namedParameterJdbcTemplate.update(INSERT, params);
    }

    @Override
    public Enchere readById(long idEnchere) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("idEnchere", idEnchere);
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ID, params, new EnchereRowMapper());
    }

    @Override
    public List<Enchere> readAll() {
        return namedParameterJdbcTemplate.query(SELECT_ALL, new EnchereRowMapper());
    }

    @Override
    public void update(Enchere enchere) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("idEnchere", enchere.getIdEnchere());
        params.addValue("dateEnchere", enchere.getDateEnchere());
        params.addValue("montant", enchere.getMontant());
        params.addValue("idUtilisateur", enchere.getEncherisseur().getIdUtilisateur());
        params.addValue("idArticle", enchere.getArticle().getIdArticle());

        namedParameterJdbcTemplate.update(UPDATE, params);
    }

    @Override
    public void delete(long idEnchere) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("idEnchere", idEnchere);
        namedParameterJdbcTemplate.update(DELETE, params);
    }

    @Override
    public Enchere selectBestEnchereByArticle(long idArticle) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("idArticle", idArticle);

        List<Enchere> encheres = namedParameterJdbcTemplate.query(SELECT_BEST_BY_ARTICLE, params, new EnchereRowMapper());
        if (encheres.isEmpty()) {
            return null;  
        } else {
            return encheres.get(0);  
        }
    }

    @Override
    public List<Enchere> selectEncheresByUtilisateur(long idUtilisateur) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("idUtilisateur", idUtilisateur);
        return namedParameterJdbcTemplate.query(SELECT_BY_UTILISATEUR, params, new EnchereRowMapper());
    }

    @Override
    public List<Enchere> selectEncheresByArticle(long idArticle) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("idArticle", idArticle);
        return namedParameterJdbcTemplate.query(SELECT_BY_ARTICLE, params, new EnchereRowMapper());
    }

    @Override
    public void deleteByArticleId(long idArticle) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("idArticle", idArticle);
        namedParameterJdbcTemplate.update(DELETE_BY_ARTICLE, params);
    }

    @Override
    public List<Enchere> rechercherEncheres(String nomArticle, int idCategorie) {
        String sql = "SELECT " +
            "e.idEnchere, e.dateEnchere, e.montant, " +
            "a.idArticle, a.nomArticle, a.dateFinEncheres, " +
            "u.idUtilisateur, u.pseudo, " +
            "v.idUtilisateur AS vendeurId, v.pseudo AS vendeurPseudo " +
            "FROM Enchere e " +
            "JOIN ArticleAVendre a ON e.idArticle = a.idArticle " +
            "JOIN Utilisateur u ON e.idUtilisateur = u.idUtilisateur " +
            "JOIN Utilisateur v ON a.idUtilisateur = v.idUtilisateur " +
            "WHERE 1=1 ";

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (nomArticle != null && !nomArticle.isBlank()) {
            sql += " AND a.nomArticle LIKE :nomArticle ";
            params.addValue("nomArticle", "%" + nomArticle + "%");
        }

        if (idCategorie > 0) {
            sql += " AND a.idCategorie = :idCategorie ";
            params.addValue("idCategorie", idCategorie);
        }

        sql += " ORDER BY e.dateEnchere DESC";

        return namedParameterJdbcTemplate.query(sql, params, new EnchereRowMapper());
    }

    private static class EnchereRowMapper implements RowMapper<Enchere> {
        @Override
        public Enchere mapRow(ResultSet rs, int rowNum) throws SQLException {
            Enchere enchere = new Enchere();
            enchere.setIdEnchere(rs.getLong("idEnchere"));
            enchere.setDateEnchere(rs.getTimestamp("dateEnchere").toLocalDateTime());
            enchere.setMontant(rs.getInt("montant"));

            Utilisateur encherisseur = new Utilisateur();
            encherisseur.setIdUtilisateur(rs.getInt("idUtilisateur"));
            encherisseur.setPseudo(rs.getString("pseudo"));
            enchere.setEncherisseur(encherisseur);

            ArticleAVendre article = new ArticleAVendre();
            article.setIdArticle(rs.getInt("idArticle"));
            article.setNomArticle(rs.getString("nomArticle"));
            article.setDateFinEncheres(rs.getTimestamp("dateFinEncheres").toLocalDateTime());

            Utilisateur vendeur = new Utilisateur();
            vendeur.setIdUtilisateur(rs.getInt("vendeurId"));
            vendeur.setPseudo(rs.getString("vendeurPseudo"));
            article.setVendeur(vendeur);

            enchere.setArticle(article);

            return enchere;
        }
    }
}