package fr.eni.encheres.dal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import fr.eni.encheres.bo.Adresse;
import fr.eni.encheres.bo.ArticleAVendre;

@Repository
public class ArticleAVendreDAOImpl implements ArticleAVendreDAO {

    // Requête d'insertion d'un article
    private static final String INSERT_ARTICLE = 
        "INSERT INTO ArticleAVendre(nomArticle, description, dateDebutEncheres, dateFinEncheres, etatVente, miseAPrix, idUtilisateur, idCategorie, idAdresseRetrait) " +
        "VALUES (:nomArticle, :description, :dateDebutEncheres, :dateFinEncheres, 0, :miseAPrix, :idUtilisateur, :idCategorie, :idAdresseRetrait)";

    // Requête de mise à jour d'un article
    private static final String UPDATE_ARTICLE = 
        "UPDATE ArticleAVendre SET nomArticle = :nomArticle, description = :description, dateDebutEncheres = :dateDebutEncheres, " +
        "dateFinEncheres = :dateFinEncheres, miseAPrix = :miseAPrix, idCategorie = :idCategorie, idAdresseRetrait = :idAdresseRetrait " +
        "WHERE idArticle = :idArticle";

    // Requête pour récupérer un article par son ID
    private static final String FIND_BY_ID = 
        "SELECT * FROM ArticleAVendre WHERE idArticle = :idArticle";

    // Requête de mise à jour du prix de vente
    private static final String UPDATE_PRIX_VENTE = 
        "UPDATE ArticleAVendre SET prixVente = :prixVente WHERE idArticle = :idArticle";

    // Requête pour trouver tous les articles en statut "en cours"
    private static final String FIND_ALL_STATUT_EN_COURS = 
        "SELECT * FROM ArticleAVendre WHERE etatVente = 1";

    // Requête pour marquer une vente comme supprimée (statut 100)
    private static final String DELETE_VENTE = 
        "UPDATE ArticleAVendre SET etatVente = 100 WHERE idArticle = :idArticle";

    // Requête pour activer les ventes qui commencent aujourd'hui
    private static final String ACTIVER_VENTE = 
        "UPDATE ArticleAVendre SET etatVente = 1 WHERE etatVente = 0 AND dateDebutEncheres = CAST(GETDATE() AS DATE)";

    // Requête pour clôturer les ventes qui se terminent aujourd'hui
    private static final String CLOTURER_VENTE = 
        "UPDATE ArticleAVendre SET etatVente = 2 WHERE etatVente = 1 AND dateFinEncheres = CAST(GETDATE() AS DATE)";

    // Requête pour marquer une vente comme livrée
    private static final String LIVRER_VENTE = 
        "UPDATE ArticleAVendre SET etatVente = 3 WHERE idArticle = :idArticle";

    // Requête pour vérifier si l'utilisateur est le propriétaire de l'article
    private static final String TROUVER_PROPRIETAIRE_ARTICLE = 
        "SELECT COUNT(*) FROM ArticleAVendre WHERE idArticle = :idArticle AND idUtilisateur = :pseudo";

    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    /**
     * Récupère un article par son ID.
     * 
     * @param idArticle L'ID de l'article à récupérer
     * @return L'article correspondant à l'ID
     */
    @Override
    public ArticleAVendre getByID(long idArticle) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("idArticle", idArticle); // Ajoute le paramètre d'ID à la requête
        return namedParameterJdbcTemplate.queryForObject(FIND_BY_ID, namedParameters, new ArticleAVendreRowMapper());
    }

    /**
     * Ajoute un nouvel article à la base de données.
     * 
     * @param article L'article à ajouter
     */
    @Override
    public void addArticle(ArticleAVendre articleAVendre) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("nomArticle", articleAVendre.getNomArticle()); // Nom de l'article
        namedParameters.addValue("description", articleAVendre.getDescription());
        namedParameters.addValue("dateDebutEncheres", articleAVendre.getDateDebutEncheres());
        namedParameters.addValue("dateFinEncheres", articleAVendre.getDateFinEncheres());
        namedParameters.addValue("miseAPrix", articleAVendre.getMiseAPrix());
        namedParameters.addValue("idUtilisateur", articleAVendre.getVendeur().getIdUtilisateur());
        namedParameters.addValue("idCategorie", articleAVendre.getCategorie().getIdCategorie());
        namedParameters.addValue("idAdresseRetrait", articleAVendre.getAdresseRetrait().getIdAdresse);
        
        // Crée un keyHolder pour récupérer l'ID généré
        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(INSERT_ARTICLE, namedParameters, keyHolder, new String[] {"idArticle"});
    }

    /**
     * Met à jour un article existant dans la base de données.
     * 
     * @param articleAVendre L'article à mettre à jour
     */
    @Override
    public void updateArticle(ArticleAVendre articleAVendre) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("nomArticle", articleAVendre.getNomArticle()); // Nom de l'article
        namedParameters.addValue("description", articleAVendre.getDescription());
        namedParameters.addValue("dateDebutEncheres", articleAVendre.getDateDebutEncheres());
        namedParameters.addValue("dateFinEncheres", articleAVendre.getDateFinEncheres());
        namedParameters.addValue("miseAPrix", articleAVendre.getMiseAPrix());
        namedParameters.addValue("idCategorie", articleAVendre.getCategorie().getIdCategorie());
        namedParameters.addValue("idAdresseRetrait", articleAVendre.getAdresseRetrait().getIdAdresse);
        namedParameters.addValue("idArticle", articleAVendre.getIdArticle()); // Ajout de l'ID pour la mise à jour
        
        int rows = namedParameterJdbcTemplate.update(UPDATE_ARTICLE, namedParameters);
        if (rows == 0) {
            throw new RuntimeException("Aucun article trouvé avec l'id : " + articleAVendre.getIdArticle());
        }
    }

    /**
     * Vérifie si un article existe dans la base de données.
     * 
     * @param idArticle L'ID de l'article à vérifier
     * @return true si l'article existe, false sinon
     */
    private boolean articleExiste(long idArticle) {
        String sql = "SELECT COUNT(*) FROM ArticleAVendre WHERE idArticle = :idArticle";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("idArticle", idArticle);
        int count = namedParameterJdbcTemplate.queryForObject(sql, parameters, Integer.class);
        return count > 0;
    }

    /**
     * Met à jour le prix de vente d'un article.
     * 
     * @param idArticle L'ID de l'article à mettre à jour
     * @param prixVente Le prix de vente à mettre à jour
     */
    @Override
    public void updatePrixVente(long idArticle, int prixVente) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("prixVente", prixVente);
        params.addValue("idArticle", idArticle);
        namedParameterJdbcTemplate.update(UPDATE_PRIX_VENTE, params);
    }

    /**
     * RowMapper pour mapper un article à partir d'un ResultSet.
     */
    public class ArticleAVendreRowMapper implements RowMapper<ArticleAVendre> {

        @Override
        public ArticleAVendre mapRow(ResultSet rs, int rowNum) throws SQLException {
            ArticleAVendre articleAVendre = new ArticleAVendre();
            articleAVendre.setIdArticle(rs.getInt("idArticle"));
            articleAVendre.setNomArticle(rs.getString("nomArticle")); // Utilisation de nomArticle
            articleAVendre.setDescription(rs.getString("description"));
            articleAVendre.setDateDebutEncheres(rs.getObject("dateDebutEncheres", LocalDateTime.class));
            articleAVendre.setDateFinEncheres(rs.getObject("dateFinEncheres", LocalDateTime.class));
            articleAVendre.setStatus(rs.getInt("etatVente"));
            articleAVendre.setMiseAPrix(rs.getInt("miseAPrix"));
            articleAVendre.setPrixVente(rs.getInt("prixVente"));
			return articleAVendre;

            // Récupère l'utilisateur qui est le

        }
    }

	@Override
	public List<ArticleAVendre> findAllStatutEnCours() {
		return namedParameterJdbcTemplate.query(FIND_ALL_STATUT_EN_COURS, new ArticleAVendreRowMapper());
	}

	@Override
	public int annulerVente(long id) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("idArticle", id);
		return namedParameterJdbcTemplate.update(DELETE_VENTE, params);
	}

	@Override
	public List<ArticleAVendre> findAllWithParameters(String nomRecherche, int categorieRecherche, int statutRecherche,
			int casUtilisationFiltres, String pseudoUtilisateurEnSession) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		// Création de mon String Builder avec la requete de base
		StringBuilder FIND_ALL_WITH_PARAMETERS = new StringBuilder("SELECT * FROM ARTICLES_A_VENDRE ");

		// Ajout du filtre des articles sur lequelles l'utilisateur a enchérit
		if (casUtilisationFiltres == 1 || casUtilisationFiltres == 2 ) {
			FIND_ALL_WITH_PARAMETERS.append("INNER JOIN ENCHERES ON ARTICLES_A_VENDRE.no_article = ENCHERES.no_article ");
		}
		
		// Ajout du parametre du Statut recherché
		namedParameters.addValue("statutRecherche", statutRecherche);
		FIND_ALL_WITH_PARAMETERS.append("WHERE statut_enchere = :statutRecherche ");

		// Ajout du filtres des article vendu par l'utilisateur
		if(pseudoUtilisateurEnSession != null) {
			namedParameters.addValue("pseudoUtilisateurEnSession", pseudoUtilisateurEnSession);
			if (casUtilisationFiltres == 1 || casUtilisationFiltres == 2 ) {
				FIND_ALL_WITH_PARAMETERS.append(" AND ENCHERES.id_utilisateur = :pseudoUtilisateurEnSession ");
			}else {
			FIND_ALL_WITH_PARAMETERS.append(" AND id_utilisateur = :pseudoUtilisateurEnSession ");
			}
			if (nomRecherche != "") {
				// Ajout des % a mon nom recherché pour la requete SQL
				String SQLNomRecherche = "%" + nomRecherche + '%';
				namedParameters.addValue("SQLNomRecherche", SQLNomRecherche);
				FIND_ALL_WITH_PARAMETERS.append("AND nom_article LIKE :SQLNomRecherche ");
			}

			if (categorieRecherche != 0) {
				namedParameters.addValue("categorieRecherche", categorieRecherche);
				FIND_ALL_WITH_PARAMETERS.append("AND no_categorie = :categorieRecherche ");
			}
			return namedParameterJdbcTemplate.query(FIND_ALL_WITH_PARAMETERS.toString(), namedParameters,
					new ArticleAVendreRowMapper());
		}
		return null;
	}
	@Override
	public int livrerVente(long id) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("idArticle", id);
		return namedParameterJdbcTemplate.update(LIVRER_VENTE, params);
	}

	@Override
	public int activerVente() {
		return namedParameterJdbcTemplate.update(ACTIVER_VENTE, new MapSqlParameterSource());
	}
	

	@Override
	public int cloturerVente() {
		return namedParameterJdbcTemplate.update(CLOTURER_VENTE, new MapSqlParameterSource());
	}

	

	@Override
	public int trouverProprietaireArticle(int idArticle, String pseudo) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("idArticle", idArticle);
		params.addValue("pseudo", pseudo);
		return namedParameterJdbcTemplate.queryForObject(TROUVER_PROPRIETAIRE_ARTICLE, params, Integer.class);
	}

}

	