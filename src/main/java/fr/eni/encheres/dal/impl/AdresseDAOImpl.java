package fr.eni.encheres.dal.impl;

import fr.eni.encheres.bo.Adresse;
import fr.eni.encheres.dal.AdresseDAO;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.List;


@Repository
public class AdresseDAOImpl implements AdresseDAO {

    private final JdbcTemplate jdbcTemplate;

    public AdresseDAOImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // CREATE
    @Override
    public void save(Adresse adresse) {
        String sql = "INSERT INTO Adresse (rue, codePostal, ville, pays,idUtilisateur) VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, adresse.getRue());
            ps.setString(2, adresse.getCodePostal());
            ps.setString(3, adresse.getVille());
            ps.setString(4, adresse.getPays());
            ps.setLong(5, adresse.getIdUtilisateur());
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key != null) {
            adresse.setIdAdresse(key.longValue());
        }
    }

    // READ (by ID)
    @Override
    public Adresse findById(long id) {
        String sql = "SELECT * FROM Adresse WHERE idAdresse = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{id}, adresseRowMapper);
    }

    @Override
    public List<Adresse> findAll() {
        return List.of();
    }

    // READ (all)
   // @Override
   // public List<Adresse> findAll() {
   //     String sql = "SELECT * FROM Adresse";

   //     return jdbcTemplate.query(sql, adresseRowMapper);
    //}

    // UPDATE
    @Override
    public void update(Adresse adresse) {
        String sql = "UPDATE Adresse SET rue = ?, codePostal = ?, ville = ?, pays = ? WHERE idAdresse = ?";

        jdbcTemplate.update(sql,
                adresse.getRue(),
                adresse.getCodePostal(),
                adresse.getVille(),
                adresse.getPays(),
                adresse.getIdAdresse());
    }

    // DELETE
    @Override
    public void delete(long id) {
        String sql = "DELETE FROM Adresse WHERE idUtilisateur = ?";

        jdbcTemplate.update(sql, id);
    }

    @Override
    public Adresse selectById(long idAdresse) {
        String sql = "SELECT idAdresse, rue, codePostal, ville, pays, idUtilisateur FROM Adresse WHERE idAdresse = ?";

        return null;
    }

    @Override
    public List<Adresse> selectAllByUtilisateurId(long idUtilisateur) {
        return List.of();
    }

    // RowMapper pour convertir un ResultSet en objet Adresse
    private final RowMapper<Adresse> adresseRowMapper = (ResultSet rs, int rowNum) -> {
        return new Adresse(
                rs.getLong("idAdresse"),
                rs.getString("rue"),
                rs.getString("codePostal"),
                rs.getString("ville"),
                rs.getString("pays"),
                rs.getLong("idUtilisateur")
        );
    };
}
