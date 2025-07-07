package fr.eni.encheres.bll;

import fr.eni.encheres.bo.Adresse;
import fr.eni.encheres.bo.Role;
import fr.eni.encheres.bo.Utilisateur;
import fr.eni.encheres.exception.BusinessException;

import java.util.List;

public interface UtilisateurService {
	
	
	void modifierProfil(Utilisateur utilisateur) throws BusinessException;
	void supprimerCompte(long idUtilisateur) throws BusinessException;
	Utilisateur selectById(long idUtilisateur) ;
	Utilisateur selectByLogin(String login) throws BusinessException ;
	Utilisateur getUtilisateurByEmail(String email) throws BusinessException;
	List<Utilisateur> selectAll() ;
	void crediterPoints(long idUtilisateur, int montant) throws BusinessException;
	void debiterPoints(long idUtilisateur, int montant) throws BusinessException;
	void devenirVendeur(long idUtilisateur) throws BusinessException;
	void creerUtilisateurAvecAdresseEtRoles(Utilisateur utilisateur, Adresse adresse, List<Role> roles)
			throws BusinessException;
}
