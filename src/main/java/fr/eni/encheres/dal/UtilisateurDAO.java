package fr.eni.encheres.dal;

import java.util.List;

import fr.eni.encheres.bo.Utilisateur;

public interface UtilisateurDAO {

	void insert(Utilisateur utilisateur);
	Utilisateur selectById(long idUtilisateur);
	Utilisateur selectByEmail(String email);
	List<Utilisateur> selectAll();
	void update(Utilisateur utilisateur);
	void delete(long idUtilisateur);
	boolean hasEmail(String email);
	Utilisateur selectByLogin(String login);
	void debiterPoints(long idUtilisateur, int montant);
	void crediterPoints(long idUtilisateur, int montant);
	void updateIdAdresse(Long idUtilisateur, Long idAdresse);


}
