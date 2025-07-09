package fr.eni.encheres.dal.impl;

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

import fr.eni.encheres.bo.ArticleAVendre;
import fr.eni.encheres.bo.Categorie;
import fr.eni.encheres.bo.EtatVente;
import fr.eni.encheres.bo.Utilisateur;
import fr.eni.encheres.dal.ArticleAVendreDAO;

@Repository
public class ArticleAVendreDAOImpl implements ArticleAVendreDAO {

	private static final String INSERT_ARTICLE = "INSERT INTO ArticleAVendre(nomArticle, description, dateDebutEncheres, dateFinEncheres, etatVente, miseAPrix, idUtilisateur, idCategorie, idAdresseRetrait) "
			+ "VALUES (:nomArticle, :description, :dateDebutEncheres, :dateFinEncheres, :etatVente, :miseAPrix, :idUtilisateur, :idCategorie, :idAdresseRetrait)";

	// Requête de mise à jour d'un article
	private static final String UPDATE_ARTICLE = "UPDATE ArticleAVendre SET nomArticle = :nomArticle, description = :description, dateDebutEncheres = :dateDebutEncheres, "
			+ "dateFinEncheres = :dateFinEncheres, miseAPrix = :miseAPrix, idCategorie = :idCategorie, idAdresseRetrait = :idAdresseRetrait "
			+ "WHERE idArticle = :idArticle";

	// Requête pour récupérer un article par son ID
	private static final String FIND_BY_ID = "SELECT a.*, u.idUtilisateur, u.pseudo, u.nom, u.prenom, u.email, "
			+ "c.idCategorie, c.libelle AS categorieLibelle " + "FROM ArticleAVendre a "
			+ "JOIN Utilisateur u ON a.idUtilisateur = u.idUtilisateur "
			+ "LEFT JOIN Categorie c ON a.idCategorie = c.idCategorie " + "WHERE a.idArticle = :idArticle";

	// Requête de mise à jour du prix de vente
	private static final String UPDATE_PRIX_VENTE = "UPDATE ArticleAVendre SET prixVente = :prixVente WHERE idArticle = :idArticle";

	// Requête pour trouver tous les articles en statut "en cours"
	private static final String FIND_ALL_STATUT_EN_COURS = "SELECT a.*, "
			+ "u.pseudo, u.nom, u.prenom, u.email, u.idUtilisateur, " + "c.idCategorie, c.libelle AS categorieLibelle "
			+ "FROM ArticleAVendre a " + "JOIN Utilisateur u ON a.idUtilisateur = u.idUtilisateur "
			+ "JOIN Categorie c ON a.idCategorie = c.idCategorie " + "WHERE a.etatVente = :etatVente";

	private static final String FIND_ALL_ARTICLE_BY_UTILISATEUR = "SELECT a.*, "
			+ "u.pseudo, u.nom, u.prenom, u.email, u.idUtilisateur, " + "c.idCategorie, c.libelle AS categorieLibelle "
			+ "FROM ArticleAVendre a " + "JOIN Utilisateur u ON a.idUtilisateur = u.idUtilisateur "
			+ "JOIN Categorie c ON a.idCategorie = c.idCategorie " + "WHERE u.pseudo = :pseudo";

	// Requête pour marquer une vente comme supprimée (statut 100)
	private static final String DELETE_VENTE = "UPDATE ArticleAVendre SET etatVente = 2 WHERE idArticle = :idArticle";

	// Requête pour activer les ventes qui commencent aujourd'hui
	private static final String ACTIVER_VENTE = "UPDATE ArticleAVendre SET etatVente = :etatCible WHERE etatVente = :etatSource AND dateDebutEncheres = CAST(GETDATE() AS DATE)";

	// Requête pour clôturer les ventes qui se terminent aujourd'hui
	private static final String CLOTURER_VENTE = "UPDATE ArticleAVendre SET etatVente = :etatTerminee WHERE etatVente = :etatEnCours AND dateFinEncheres = CAST(GETDATE() AS DATE)";

	// Requête pour marquer une vente comme livrée
	private static final String LIVRER_VENTE = "UPDATE ArticleAVendre SET etatVente = 3 WHERE idArticle = :idArticle";

	// Requête pour vérifier si l'utilisateur est le propriétaire de l'article
	private static final String TROUVER_PROPRIETAIRE_ARTICLE = "SELECT COUNT(*) FROM ArticleAVendre a "
			+ "JOIN Utilisateur u ON a.idUtilisateur = u.idUtilisateur "
			+ "WHERE a.idArticle = :idArticle AND u.pseudo = :pseudo";

	private static final String FIND_EN_COURS_WITH_FILTERS = "SELECT a.*, u.pseudo, u.nom, u.prenom, u.email, u.idUtilisateur, "
			+ "c.idCategorie, c.libelle AS categorieLibelle " + "FROM ArticleAVendre a "
			+ "JOIN Utilisateur u ON a.idUtilisateur = u.idUtilisateur "
			+ "LEFT JOIN Categorie c ON a.idCategorie = c.idCategorie " + "WHERE a.etatVente = :etatVente "
			+ "AND (:nomRecherche IS NULL OR a.nomArticle LIKE :nomRecherche) "
			+ "AND (:categorieRecherche = 0 OR a.idCategorie = :categorieRecherche)";

	private static final String FIND_ARTICLES_BY_VENDEUR_EN_COURS = "SELECT a.*, u.pseudo, u.nom, u.prenom, u.email, u.idUtilisateur, "
			+ "c.idCategorie, c.libelle AS categorieLibelle " + "FROM ArticleAVendre a "
			+ "JOIN Utilisateur u ON a.idUtilisateur = u.idUtilisateur "
			+ "LEFT JOIN Categorie c ON a.idCategorie = c.idCategorie " + "WHERE u.pseudo = :pseudo "
			+ "AND a.etatVente = :etatVente";
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
		namedParameters.addValue("nomArticle", articleAVendre.getNomArticle());
		namedParameters.addValue("description", articleAVendre.getDescription());
		namedParameters.addValue("dateDebutEncheres", articleAVendre.getDateDebutEncheres());
		namedParameters.addValue("dateFinEncheres", articleAVendre.getDateFinEncheres());
		namedParameters.addValue("miseAPrix", articleAVendre.getMiseAPrix());
		namedParameters.addValue("idUtilisateur", articleAVendre.getVendeur().getIdUtilisateur());
		namedParameters.addValue("idCategorie", articleAVendre.getCategorie().getIdCategorie());
		namedParameters.addValue("idAdresseRetrait", articleAVendre.getAdresseRetrait().getIdAdresse());

		LocalDateTime now = LocalDateTime.now();
		int codeEtat = articleAVendre.getDateDebutEncheres().isAfter(now) ? EtatVente.NON_DEMARREE.getCode()
				: EtatVente.EN_COURS.getCode();
		namedParameters.addValue("etatVente", codeEtat);

		KeyHolder keyHolder = new GeneratedKeyHolder();
		namedParameterJdbcTemplate.update(INSERT_ARTICLE, namedParameters, keyHolder, new String[] { "idArticle" });

		Number key = keyHolder.getKey();
		if (key != null) {
			articleAVendre.setIdArticle(key.longValue());
		}

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
		namedParameters.addValue("idAdresseRetrait", articleAVendre.getAdresseRetrait().getIdAdresse());
		namedParameters.addValue("idArticle", articleAVendre.getIdArticle());

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
			articleAVendre.setIdArticle(rs.getLong("idArticle"));
			articleAVendre.setNomArticle(rs.getString("nomArticle"));
			articleAVendre.setDescription(rs.getString("description"));
			articleAVendre.setDateDebutEncheres(rs.getTimestamp("dateDebutEncheres").toLocalDateTime());
			articleAVendre.setDateFinEncheres(rs.getTimestamp("dateFinEncheres").toLocalDateTime());
			int code = rs.getInt("etatVente");
			articleAVendre.setEtatVente(EtatVente.fromCode(code));
			articleAVendre.setMiseAPrix(rs.getInt("miseAPrix"));
			articleAVendre.setPrixVente(rs.getInt("prixVente"));

			Utilisateur vendeur = new Utilisateur();
			vendeur.setIdUtilisateur(rs.getInt("idUtilisateur"));
			vendeur.setPseudo(rs.getString("pseudo"));
			vendeur.setNom(rs.getString("nom"));
			vendeur.setPrenom(rs.getString("prenom"));
			vendeur.setEmail(rs.getString("email"));
			articleAVendre.setVendeur(vendeur);

			// Récupérer la catégorie pour l'affichage dabs la liste
			Categorie categorie = new Categorie();
			categorie.setIdCategorie(rs.getInt("idCategorie"));
			categorie.setLibelle(rs.getString("categorieLibelle"));
			articleAVendre.setCategorie(categorie);

			return articleAVendre;
		}
	}

	@Override
	public List<ArticleAVendre> findAllStatutEnCours() {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("etatVente", EtatVente.EN_COURS.getCode());
		return namedParameterJdbcTemplate.query(FIND_ALL_STATUT_EN_COURS, params, new ArticleAVendreRowMapper());
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

		StringBuilder sql = new StringBuilder("SELECT a.*, u.pseudo, u.nom, u.prenom, u.email, u.idUtilisateur, "
				+ "c.idCategorie, c.libelle AS categorieLibelle " + "FROM ArticleAVendre a "
				+ "JOIN Utilisateur u ON a.idUtilisateur = u.idUtilisateur "
				+ "LEFT JOIN Categorie c ON a.idCategorie = c.idCategorie ");

		if (casUtilisationFiltres == 1 || casUtilisationFiltres == 2) {
			sql.append("JOIN Enchere e ON a.idArticle = e.idArticle ");
		}

		sql.append("WHERE 1=1 ");

		// Filtre sur statut via l'enum EtatVente j'ai creeé un converter Int dans enum
		EtatVente etat = EtatVente.fromInt(statutRecherche);
		sql.append("AND a.etatVente = :statutRecherche ");
		namedParameters.addValue("statutRecherche", etat.getCode());

		if (pseudoUtilisateurEnSession != null && !pseudoUtilisateurEnSession.isEmpty()) {
			if (casUtilisationFiltres == 1 || casUtilisationFiltres == 2) {
				sql.append(
						"AND e.idUtilisateur = (SELECT idUtilisateur FROM Utilisateur WHERE pseudo = :pseudoUtilisateurEnSession) ");
			} else {
				sql.append(
						"AND a.idUtilisateur = (SELECT idUtilisateur FROM Utilisateur WHERE pseudo = :pseudoUtilisateurEnSession) ");
			}
			namedParameters.addValue("pseudoUtilisateurEnSession", pseudoUtilisateurEnSession);
		}

		// Filtre sur nom d'article
		if (nomRecherche != null && !nomRecherche.isBlank()) {
			sql.append("AND a.nomArticle LIKE :nomRecherche ");
			namedParameters.addValue("nomRecherche", "%" + nomRecherche + "%");
		}

		// Filtre sur catégorie
		if (categorieRecherche != 0) {
			sql.append("AND a.idCategorie = :categorieRecherche ");
			namedParameters.addValue("categorieRecherche", categorieRecherche);
		}

		return namedParameterJdbcTemplate.query(sql.toString(), namedParameters, new ArticleAVendreRowMapper());
	}

	@Override
	public int livrerVente(long id) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("idArticle", id);
		return namedParameterJdbcTemplate.update(LIVRER_VENTE, params);
	}

	@Override
	public int activerVente() {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("etatCible", EtatVente.EN_COURS.getCode());
		params.addValue("etatSource", EtatVente.NON_DEMARREE.getCode());
		return namedParameterJdbcTemplate.update(ACTIVER_VENTE, params);
	}

	@Override
	public int cloturerVente() {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("etatTerminee", EtatVente.TERMINEE.getCode()); // "Terminee"
		params.addValue("etatEnCours", EtatVente.EN_COURS.getCode()); // "En cours"
		return namedParameterJdbcTemplate.update(CLOTURER_VENTE, params);
	}

	@Override
	public int trouverProprietaireArticle(int idArticle, String pseudo) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("idArticle", idArticle);
		params.addValue("pseudo", pseudo);
		return namedParameterJdbcTemplate.queryForObject(TROUVER_PROPRIETAIRE_ARTICLE, params, Integer.class);
	}

	// -------- Mehodes dans le controller
	@Override
	public List<ArticleAVendre> rechercherArticlesEnCours(String nomRecherche, int categorieRecherche) {
		MapSqlParameterSource params = new MapSqlParameterSource();

		params.addValue("etatVente", EtatVente.EN_COURS.getCode());

		if (nomRecherche == null || nomRecherche.isBlank()) {
			params.addValue("nomRecherche", null);
		} else {
			params.addValue("nomRecherche", "%" + nomRecherche + "%");
		}

		params.addValue("categorieRecherche", categorieRecherche);

		return namedParameterJdbcTemplate.query(FIND_EN_COURS_WITH_FILTERS, params, new ArticleAVendreRowMapper());
	}

	@Override
	public List<ArticleAVendre> findArticlesEnCoursByVendeur(String pseudo) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("pseudo", pseudo);
		params.addValue("etatVente", EtatVente.EN_COURS.getCode());
		return namedParameterJdbcTemplate.query(FIND_ARTICLES_BY_VENDEUR_EN_COURS, params,
				new ArticleAVendreRowMapper());
	}

	@Override
	public List<ArticleAVendre> findAllByUtilisateur(String pseudo) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("pseudo", pseudo);

		return namedParameterJdbcTemplate.query(FIND_ALL_ARTICLE_BY_UTILISATEUR, params, new ArticleAVendreRowMapper());
	}

}
