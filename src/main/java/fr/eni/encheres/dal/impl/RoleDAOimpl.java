package fr.eni.encheres.dal.impl;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import fr.eni.encheres.bo.Role;
import fr.eni.encheres.dal.RoleDAO;

@Repository
public class RoleDAOimpl implements RoleDAO {

    private static final String FIND_ALL = "SELECT idRole, libelle FROM Role";
    private static final String FIND_BY_ID = "SELECT idRole, libelle FROM Role WHERE idRole = :idRole";
    private static final String FIND_BY_LIBELLE = "SELECT idRole, libelle FROM Role WHERE libelle = :libelle";
    private static final String INSERT_ROLE = "INSERT INTO Role (libelle) VALUES (:libelle)";
    private static final String UPDATE_ROLE = "UPDATE Role SET libelle = :libelle WHERE idRole = :idRole";
    private static final String DELETE_ROLE = "DELETE FROM Role WHERE idRole = :idRole";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public RoleDAOimpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<Role> selectAll() {
        return namedParameterJdbcTemplate.query(FIND_ALL, new BeanPropertyRowMapper<>(Role.class));
    }

    @Override
    public Role selectById(int idRole) {
        MapSqlParameterSource params = new MapSqlParameterSource("idRole", idRole);
        try {
            return namedParameterJdbcTemplate.queryForObject(FIND_BY_ID, params, new BeanPropertyRowMapper<>(Role.class));
        } catch (EmptyResultDataAccessException e) {
            return null; 
        }
    }

    @Override
    public Role selectByLibelle(String libelle) {
        MapSqlParameterSource params = new MapSqlParameterSource("libelle", libelle);
        try {
            return namedParameterJdbcTemplate.queryForObject(FIND_BY_LIBELLE, params, new BeanPropertyRowMapper<>(Role.class));
        } catch (EmptyResultDataAccessException e) {
            return null; 
        }
    }

    @Override
    public void insert(Role role) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("libelle", role.getLibelle());
 
        KeyHolder keyHolder = new GeneratedKeyHolder();
        int rows = namedParameterJdbcTemplate.update(INSERT_ROLE, params, keyHolder, new String[]{"idRole"});
        if (rows == 0) {
            throw new RuntimeException("Échec de l'insertion du rôle.");
        }
        Number key = keyHolder.getKey();
        if (key != null) {
            role.setIdRole(key.intValue());
        }
    }

    @Override
    public void update(Role role) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("idRole", role.getIdRole());
        params.addValue("libelle", role.getLibelle());

        int rows = namedParameterJdbcTemplate.update(UPDATE_ROLE, params);
        if (rows == 0) {
            throw new RuntimeException("Aucun rôle mis à jour. ID introuvable : " + role.getIdRole());
        }
    }

    @Override
    public void delete(int idRole) {
        MapSqlParameterSource params = new MapSqlParameterSource("idRole", idRole);

        int rows = namedParameterJdbcTemplate.update(DELETE_ROLE, params);
        if (rows == 0) {
            throw new RuntimeException("Suppression échouée. Aucun rôle trouvé pour l'ID : " + idRole);
        }
    }
}
