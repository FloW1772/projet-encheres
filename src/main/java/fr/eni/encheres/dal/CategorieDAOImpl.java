package fr.eni.encheres.dal;

import java.util.List;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import fr.eni.encheres.bo.Categorie;

@Repository
public class CategorieDAOImpl implements CategorieDAO{

	private final NamedParameterJdbcTemplate jdbcTemplate;
	
	public CategorieDAOImpl(NamedParameterJdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;

	}

	@Override
    public List<Categorie> findAll() {
        String sql = "SELECT idCategorie, libelle FROM CATEGORIE";
        return jdbcTemplate.query(sql, 
            (rs, rowNum) -> new Categorie(
                rs.getInt("idCategorie"),
                rs.getString("libelle")
            )
        );
    }

    @Override
    public void insert(Categorie categorie) {
       //TODO A implémenter au besoin
    }
		
	
}
