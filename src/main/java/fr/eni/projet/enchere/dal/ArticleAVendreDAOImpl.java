package fr.eni.projet.enchere.dal;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import fr.eni.projet.enchere.bo.ArticleAVendre;


public class ArticleAVendreDAOImpl implements ArticleAVendreDAO {

	private static final String INSERT_ARTICLE = "INSERT INTO ARTICLES_A_VENDRE(nom_article, description, date_debut_encheres, date_fin_encheres, statut_enchere, prix_initial, id_utilisateur, no_categorie, no_adresse_retrait) VALUES "
			+ " (:nom, :description, :dateDebutEncheres, :dateFinEncheres, 0, :prixInitial, :vendeur, :categorie, :adresse)";

	private static final String UPDATE_ARTICLE = "UPDATE ARTICLES_A_VENDRE SET nom_article = :nom, description = :description, date_debut_encheres = :dateDebutEncheres, date_fin_encheres = :dateFinEncheres,prix_initial = :prixInitial, no_categorie = :categorie, no_adresse_retrait = :adresse WHERE no_article=:id";

	private static final String FIND_BY_ID = "SELECT * FROM ARTICLES_A_VENDRE WHERE no_article = :id";
	private static final String UPDATE_PRIX_VENTE = "UPDATE articles_a_vendre SET prix_vente=:prixVente WHERE no_article=:idArticle";
	private static final String FIND_ALL_STATUT_EN_COURS = "SELECT * FROM ARTICLES_A_VENDRE WHERE statut_enchere = 1";
	private static final String DELETE_VENTE = "UPDATE articles_a_vendre SET statut_enchere=100 WHERE no_article=:idArticle";
	private static final String ACTIVER_VENTE = "UPDATE articles_a_vendre SET statut_enchere=1 WHERE statut_enchere=0 AND date_debut_encheres=CAST(GETDATE() as DATE)";
	private static final String CLOTURER_VENTE = "UPDATE articles_a_vendre SET statut_enchere=2 WHERE statut_enchere=1 AND date_fin_encheres=CAST(GETDATE() as DATE)";

	private static final String LIVRER_VENTE = "UPDATE articles_a_vendre SET statut_enchere=3 WHERE no_article=:idArticle";

	private static final String TROUVER_PROPRIETAIRE_ARTICLE = "SELECT COUNT(*) FROM articles_a_vendre WHERE no_article=:idArticle AND id_utilisateur=:pseudo";

	
	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	
	
	@Override
	public ArticleAVendre getByID(long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("id", id);
		return namedParameterJdbcTemplate.queryForObject(FIND_BY_ID, namedParameters, new ArticleAVendreRowMapper());
	}
	

	@Override
	public void addArticle(ArticleAVendre articleAVendre) {
		MapSqlParameterSource namedParameters = preparerParamValidationArticle(articleAVendre);
		namedParameters.addValue("vendeur", articleAVendre.getVendeur().getPseudo());
		namedParameterJdbcTemplate.update(INSERT_ARTICLE, namedParameters);
	}
		


	@Override
	public void updatePrixVente(long id, int montant) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("prixVente", montant);
		params.addValue("idArticle", id);
		namedParameterJdbcTemplate.update(UPDATE_PRIX_VENTE, params);
		
	}

	@Override
	public List<ArticleAVendre> findAllStatutEnCours() {
		return namedParameterJdbcTemplate.query(FIND_ALL_STATUT_EN_COURS, new ArticleAVendreRowMapper());
	}

	@Override
	public int annulerVente(long idArticle) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("idArticle", idArticle);
		return namedParameterJdbcTemplate.update(DELETE_VENTE, params);
	}
	@Override
	public int activerVente() {
		return namedParameterJdbcTemplate.update(ACTIVER_VENTE, new MapSqlParameterSource());
	}

	@Override
	public void updateArticle(ArticleAVendre articleAVendre) {
		MapSqlParameterSource namedParameters = preparerParamValidationArticle(articleAVendre);
		namedParameters.addValue("idArticle", articleAVendre.getIdArticle());
		BusinessException be = new BusinessException();
		
		if (articleExiste(articleAVendre.getIdArticle())) {
		namedParameterJdbcTemplate.update(UPDATE_ARTICLE, namedParameters);
		} else {
	        throw be; 
	    }
	}

	private boolean articleExiste(long id) {
		 String sql = "SELECT COUNT(*) FROM articles_a_vendre WHERE no_article = :id";
		 MapSqlParameterSource parameters = new MapSqlParameterSource();
		 parameters.addValue("id", id);
		 int count = namedParameterJdbcTemplate.queryForObject(sql, parameters, Integer.class);
		 return count > 0;
	}
	
	private MapSqlParameterSource preparerParamValidationArticle(ArticleAVendre articleAVendre) {
	    MapSqlParameterSource namedParameters = new MapSqlParameterSource();
	    namedParameters.addValue("nom", articleAVendre.getNomArticle());
	    namedParameters.addValue("description", articleAVendre.getDescription());
	    namedParameters.addValue("dateDebutEncheres", convertirDate(articleAVendre.getDateDebutEncheres()));
	    namedParameters.addValue("dateFinEncheres", convertirDate(articleAVendre.getDateFinEncheres()));
	    namedParameters.addValue("miseAPrix", articleAVendre.getMiseAPrix());
	    namedParameters.addValue("categorie", articleAVendre.getCategorie().getId());
	    namedParameters.addValue("adresse", articleAVendre.getAdresseRetrait().getId());
	    return namedParameters;
	}
	public Date convertirDate(LocalDate localDate) {
	    return Date.valueOf(localDate);
	}
	
	
	@Override
	public List<ArticleAVendre> findAllWithParameters(String nomRecherche, int categorieRecherche, int statutRecherche,
			int casUtilisationFiltres, String pseudoUtilisateurEnSession) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int cloturerVente() {
		return namedParameterJdbcTemplate.update(CLOTURER_VENTE, new MapSqlParameterSource());
	}

	@Override
	public int livrerVente(long idArticle) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("idArticle", idArticle);
		return namedParameterJdbcTemplate.update(LIVRER_VENTE, params);
	}
	
	
	@Override
	public int trouverProprietaireArticle(int idArticle, String pseudo) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("idArticle", idArticle);
		params.addValue("pseudo", pseudo);
		return namedParameterJdbcTemplate.queryForObject(TROUVER_PROPRIETAIRE_ARTICLE, params, Integer.class);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
