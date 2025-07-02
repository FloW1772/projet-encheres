package fr.eni.encheres.dal.impl;

import fr.eni.encheres.dal.UtilisateurRoleDAO;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UtilisateurRoleDAOImpl implements UtilisateurRoleDAO {

    private static final String INSERT_UTILISATEUR_ROLE =
            "INSERT INTO utilisateur_role (idUtilisateur, idRole) VALUES (:idUtilisateur, :idRole)";
    
    private static final String DELETE_BY_USER_ID =
            "DELETE FROM utilisateur_role WHERE idUtilisateur = :idUtilisateur";
    
    private static final String SELECT_ROLE_IDS_BY_USER_ID =
            "SELECT idRole FROM utilisateur_role WHERE idUtilisateur = :idUtilisateur";
    
    private static final String SELECT_USER_IDS_BY_ROLE_ID =
            "SELECT idUtilisateur FROM utilisateur_role WHERE idRole = :idRole";
    
    //--- a voir plus tard ----------
    private static final String UPDATE_ROLE =
            "UPDATE utilisateur_role SET idRole = :newIdRole WHERE idUtilisateur = :idUtilisateur AND idRole = :oldIdRole";

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UtilisateurRoleDAOImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insert(long idUtilisateur, long idRole) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idUtilisateur", idUtilisateur)
                .addValue("idRole", idRole);

        jdbcTemplate.update(INSERT_UTILISATEUR_ROLE, params);
    }

    @Override
    public void deleteByUserId(long idUtilisateur) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idUtilisateur", idUtilisateur);

        jdbcTemplate.update(DELETE_BY_USER_ID, params);
    }

    @Override
    public List<Integer> findRoleIdsByUserId(long idUtilisateur) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idUtilisateur", idUtilisateur);

        return jdbcTemplate.queryForList(SELECT_ROLE_IDS_BY_USER_ID, params, Integer.class);
    }

    @Override
    public List<Long> findUserIdsByRoleId(int idRole) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idRole", idRole);

        return jdbcTemplate.queryForList(SELECT_USER_IDS_BY_ROLE_ID, params, Long.class);
    }
    
    
///---------- a  voir plus tard ------
    @Override
    public void updateRoleForUser(long idUtilisateur, int oldIdRole, int newIdRole) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idUtilisateur", idUtilisateur)
                .addValue("oldIdRole", oldIdRole)
                .addValue("newIdRole", newIdRole);

        int rows = jdbcTemplate.update(UPDATE_ROLE, params);
        if (rows == 0) {
            throw new RuntimeException("Aucun rôle mis à jour pour l'utilisateur ID " + idUtilisateur +
                    " (rôle actuel : " + oldIdRole + ")");
        }
    }
}
