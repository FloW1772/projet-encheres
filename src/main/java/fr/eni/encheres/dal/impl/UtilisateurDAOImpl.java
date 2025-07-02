package fr.eni.encheres.dal.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import fr.eni.encheres.bo.Adresse;
import fr.eni.encheres.bo.Utilisateur;
import fr.eni.encheres.dal.UtilisateurDAO;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class UtilisateurDAOImpl implements UtilisateurDAO {

	private static final String SELECT_WITH_ADRESSE_BASE = "SELECT u.idUtilisateur, u.pseudo, u.nom, u.prenom, u.email, u.telephone, u.credit, "
			+ "a.idAdresse, a.rue, a.codePostal, a.ville " + "FROM UTILISATEUR u "
			+ "LEFT JOIN ADRESSE a ON u.idUtilisateur = a.idUtilisateur ";
	private static final String FIND_BY_ID = SELECT_WITH_ADRESSE_BASE + " WHERE u.idUtilisateur = :id";
	private static final String FIND_BY_EMAIL = SELECT_WITH_ADRESSE_BASE + " WHERE u.email = :email";
	private static final String SELECT_BY_LOGIN = SELECT_WITH_ADRESSE_BASE + " WHERE u.pseudo = :pseudo";
	private static final String FIND_ALL_WITH_ADRESSE = "SELECT u.idUtilisateur, u.pseudo, u.nom, u.prenom, u.email, u.telephone, u.credit, "
			+ "a.idAdresse, a.rue, a.codePostal, a.ville " + "FROM UTILISATEUR u "
			+ "LEFT JOIN ADRESSE a ON u.idUtilisateur = a.idUtilisateur";

	private static final String CREATE_UTILISATEUR = "INSERT INTO UTILISATEUR (pseudo, nom, prenom, email, telephone, motDePasse, credit) "
			+ "VALUES (:pseudo, :nom, :prenom, :email, :telephone, :motDePasse, :credit)";

	private static final String UPDATE_UTILISATEUR = "UPDATE UTILISATEUR SET pseudo = :pseudo, nom = :nom, prenom = :prenom, "
			+ "telephone = :telephone, motDePasse = :motDePasse, credit = :credit " + "WHERE email = :email";
	private static final String DELETE = "DELETE FROM UTILISATEUR WHERE idUtilisateur = :idUtilisateur";
	private static final String COUNT_BY_EMAIL = "SELECT COUNT(*) FROM UTILISATEUR WHERE email = :email";
	private static final String  SET_DEBITER = "UPDATE Utilisateur SET creditPoints = creditPoints - :montant WHERE idUtilisateur = :idUtilisateur AND creditPoints >= :montant";
	private static final String  SET_CREDITER = "UPDATE Utilisateur SET creditPoints = creditPoints + :montant WHERE idUtilisateur = :idUtilisateur";
	private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public UtilisateurDAOImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	@Override
	public Utilisateur selectById(long id) {
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("id", id);
		try {
			return this.namedParameterJdbcTemplate.queryForObject(FIND_BY_ID, map,
					new UtilisateurAvecAdresseRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public Utilisateur selectByEmail(String email) {
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("email", email);
		try {
			return this.namedParameterJdbcTemplate.queryForObject(FIND_BY_EMAIL, map,
					new UtilisateurAvecAdresseRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<Utilisateur> selectAll() {
		return namedParameterJdbcTemplate.query(FIND_ALL_WITH_ADRESSE, new UtilisateurAvecAdresseRowMapper());
	}

	@Override
	public void insert(Utilisateur utilisateur) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("pseudo", utilisateur.getPseudo());
		params.addValue("nom", utilisateur.getNom());
		params.addValue("prenom", utilisateur.getPrenom());
		params.addValue("email", utilisateur.getEmail());
		params.addValue("telephone", utilisateur.getTelephone());
		params.addValue("motDePasse", utilisateur.getMotDePasse());
		params.addValue("credit", utilisateur.getCredit());

		KeyHolder keyHolder = new GeneratedKeyHolder();

		namedParameterJdbcTemplate.update(CREATE_UTILISATEUR, params, keyHolder, new String[] { "idUtilisateur" });
		Number key = keyHolder.getKey();
		if (key != null) {
			utilisateur.setIdUtilisateur(key.longValue());
		}
	}

	@Override
	public void update(Utilisateur utilisateur) {
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("pseudo", utilisateur.getPseudo());
		map.addValue("nom", utilisateur.getNom());
		map.addValue("prenom", utilisateur.getPrenom());
		map.addValue("telephone", utilisateur.getTelephone());
		map.addValue("motDePasse", utilisateur.getMotDePasse());
		map.addValue("credit", utilisateur.getCredit());
		map.addValue("email", utilisateur.getEmail());

		int rows = namedParameterJdbcTemplate.update(UPDATE_UTILISATEUR, map);
		if (rows == 0) {
			throw new RuntimeException("Aucun utilisateur trouvé avec l'email : " + utilisateur.getEmail());
		}
	}

	@Override
	public void delete(long idUtilisateur) {
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("idUtilisateur", idUtilisateur);
		int rows = namedParameterJdbcTemplate.update(DELETE, map);
		if (rows == 0) {
			throw new RuntimeException("Suppression échouée. Aucun utilisateur trouvé pour : " + idUtilisateur);
		}
	}

	@Override
	public boolean hasEmail(String email) {
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("email", email);

		Integer nbEmail = this.namedParameterJdbcTemplate.queryForObject(COUNT_BY_EMAIL, map, Integer.class);
		return nbEmail != null && nbEmail != 0;
	}

	@Override
	public Utilisateur selectByLogin(String pseudo) {
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue("pseudo", pseudo);
		try {
			return namedParameterJdbcTemplate.queryForObject(SELECT_BY_LOGIN, map,
					new UtilisateurAvecAdresseRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}


	// ------- pour recuperer la vue avec l'adresse

	private static class UtilisateurAvecAdresseRowMapper implements RowMapper<Utilisateur> {
		@Override
		public Utilisateur mapRow(ResultSet rs, int rowNum) throws SQLException {
			Utilisateur utilisateur = new Utilisateur();
			utilisateur.setIdUtilisateur(rs.getLong("idUtilisateur"));
			utilisateur.setPseudo(rs.getString("pseudo"));
			utilisateur.setNom(rs.getString("nom"));
			utilisateur.setPrenom(rs.getString("prenom"));
			utilisateur.setEmail(rs.getString("email"));
			utilisateur.setTelephone(rs.getString("telephone"));
			utilisateur.setCredit(rs.getInt("credit"));

			long idAdresse = rs.getLong("idAdresse");
			if (!rs.wasNull()) {
				Adresse adresse = new Adresse();
				adresse.setIdAdresse(idAdresse);
				adresse.setRue(rs.getString("rue"));
				adresse.setCodePostal(rs.getString("codePostal"));
				adresse.setVille(rs.getString("ville"));
				adresse.setIdUtilisateur(utilisateur.getIdUtilisateur());

				utilisateur.setAdresse(adresse);
			}

			return utilisateur;
		}
	}


	@Override
	public void debiterPoints(long idUtilisateur, int montant) {
		 MapSqlParameterSource params = new MapSqlParameterSource();
		    params.addValue("montant", montant);
		    params.addValue("idUtilisateur", idUtilisateur);
		    int rowsAffected = namedParameterJdbcTemplate.update(SET_DEBITER, params);
		    if(rowsAffected == 0) {
		        throw new RuntimeException("Crédit insuffisant ou utilisateur non trouvé");
		    }
		
	}

	@Override
	public void crediterPoints(long idUtilisateur, int montant) {
		    MapSqlParameterSource params = new MapSqlParameterSource();
		    params.addValue("montant", montant);
		    params.addValue("idUtilisateur", idUtilisateur);
		    namedParameterJdbcTemplate.update(SET_CREDITER, params);
		
	}


}
