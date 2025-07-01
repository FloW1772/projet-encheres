package fr.eni.encheres.dal;

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

@Repository
public class EnchereDAOImpl implements EnchereDAO {

    private final String INSERT = "INSERT INTO ENCHERE(dateEnchere, montant, idUtilisateur, idArticle) VALUES (:dateEnchere, :montant, :idUtilisateur, :idArticle)";
    private final String SELECT_BY_ID = "SELECT * FROM ENCHERE WHERE idEnchere = :idEnchere";
    private final String SELECT_ALL = "SELECT * FROM ENCHERE";
    private final String UPDATE = "UPDATE ENCHERE SET dateEnchere = :dateEnchere, montant = :montant, idUtilisateur = :idUtilisateur, idArticle = :idArticle WHERE idEnchere = :idEnchere";
    private final String DELETE = "DELETE FROM ENCHERE WHERE idEnchere = :idEnchere";
    private final String SELECT_BEST_BY_ARTICLE = "SELECT TOP 1 * FROM ENCHERE WHERE idArticle = :idArticle ORDER BY montant DESC";
    private final String SELECT_BY_UTILISATEUR = "SELECT * FROM ENCHERE WHERE idUtilisateur = :idUtilisateur";
    private final String SELECT_BY_ARTICLE = "SELECT * FROM ENCHERE WHERE idArticle = :idArticle";
    private final String DELETE_BY_ARTICLE = "DELETE FROM ENCHERE WHERE idArticle = :idArticle";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public EnchereDAOImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void create(Enchere enchere) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("dateEnchere", enchere.getDateEnchere());
        params.addValue("montant", enchere.getMontant());
        params.addValue("idUtilisateur", enchere.getEncherisseur().getIdUtilisateur());
        params.addValue("idArticle", enchere.getArticle().getIdArticle());

        jdbcTemplate.update(INSERT, params);
    }

    @Override
    public Enchere readById(long idEnchere) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("idEnchere", idEnchere);
        return jdbcTemplate.queryForObject(SELECT_BY_ID, params, new EnchereRowMapper());
    }

    @Override
    public List<Enchere> readAll() {
        return jdbcTemplate.query(SELECT_ALL, new EnchereRowMapper());
    }

    @Override
    public void update(Enchere enchere) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("idEnchere", enchere.getIdEnchere());
        params.addValue("dateEnchere", enchere.getDateEnchere());
        params.addValue("montant", enchere.getMontant());
        params.addValue("idUtilisateur", enchere.getEncherisseur().getIdUtilisateur());
        params.addValue("idArticle", enchere.getArticle().getIdArticle());

        jdbcTemplate.update(UPDATE, params);
    }

    @Override
    public void delete(long idEnchere) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("idEnchere", idEnchere);
        jdbcTemplate.update(DELETE, params);
    }

    @Override
    public Enchere selectBestEnchereByArticle(long idArticle) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("idArticle", idArticle);
        return jdbcTemplate.queryForObject(SELECT_BEST_BY_ARTICLE, params, new EnchereRowMapper());
    }

    @Override
    public List<Enchere> selectEncheresByUtilisateur(long idUtilisateur) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("idUtilisateur", idUtilisateur);
        return jdbcTemplate.query(SELECT_BY_UTILISATEUR, params, new EnchereRowMapper());
    }

    @Override
    public List<Enchere> selectEncheresByArticle(long idArticle) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("idArticle", idArticle);
        return jdbcTemplate.query(SELECT_BY_ARTICLE, params, new EnchereRowMapper());
    }

    @Override
    public void deleteByArticleId(long idArticle) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("idArticle", idArticle);
        jdbcTemplate.update(DELETE_BY_ARTICLE, params);
    }
    

    private static class EnchereRowMapper implements RowMapper<Enchere> {
        @Override
        public Enchere mapRow(ResultSet rs, int rowNum) throws SQLException {
            Enchere enchere = new Enchere();
            enchere.setIdEnchere(rs.getLong("idEnchere"));
            enchere.setDateEnchere(rs.getTimestamp("dateEnchere").toLocalDateTime());
            enchere.setMontant(rs.getInt("montant"));

            Utilisateur utilisateur = new Utilisateur();
            utilisateur.setIdUtilisateur(rs.getInt("idUtilisateur"));
            enchere.setEncherisseur(utilisateur);

            ArticleAVendre article = new ArticleAVendre();
            article.setIdArticle(rs.getInt("idArticle"));
            enchere.setArticle(article);

            return enchere;
        }
    }
}