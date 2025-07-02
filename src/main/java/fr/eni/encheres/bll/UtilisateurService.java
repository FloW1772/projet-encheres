package fr.eni.encheres.bll;

import fr.eni.encheres.bo.Adresse;
import fr.eni.encheres.bo.Role;
import fr.eni.encheres.bo.Utilisateur;
import fr.eni.encheres.exception.BusinessException;

import java.util.List;

public interface UtilisateurService {
	
	void creerUtilisateurAvecAdresseEtRole(Utilisateur utilisateur, Adresse adresse, Role Role) throws BusinessException;
	void modifierProfil(Utilisateur utilisateur) throws BusinessException;
	void supprimerCompte(long idUtilisateur) throws BusinessException;
	Utilisateur selectById(int idUtilisateur) ;
	Utilisateur selectByLogin(String login) ;
	Utilisateur getUtilisateurByEmail(String email) throws BusinessException;
	List<Utilisateur> selectAll() ;

}
