package fr.eni.encheres.dal.impl;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import fr.eni.encheres.bo.Categorie;
import fr.eni.encheres.dal.CategorieDAO;

@Repository
public class CategorieDAOImpl implements CategorieDAO {

    private final String SELECT_ALL = "SELECT idCategorie, libelle FROM CATEGORIE";
    private final String INSERT = "INSERT INTO CATEGORIE(libelle) VALUES (:libelle)";
    private final String SELECT_BY_ID = "SELECT idCategorie, libelle FROM CATEGORIE WHERE idCategorie = :idCategorie";
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public CategorieDAOImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Categorie> findAll() {
        return jdbcTemplate.query(SELECT_ALL,
            (rs, rowNum) -> new Categorie(
                rs.getInt("idCategorie"),
                rs.getString("libelle")
            )
        );
    }
 
    @Override
    public void insert(Categorie categorie) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("libelle", categorie.getLibelle());

        jdbcTemplate.update(INSERT, params);
    }
    
    @Override
    public Categorie findById(int idCategorie) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("idCategorie", idCategorie);  // 

        return jdbcTemplate.queryForObject(SELECT_BY_ID, params, 
            (rs, rowNum) -> new Categorie(
                rs.getInt("idCategorie"),
                rs.getString("libelle")
            )
        );
    }

    
}


	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

