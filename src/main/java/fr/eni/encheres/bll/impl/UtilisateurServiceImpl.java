package fr.eni.encheres.bll.impl;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.eni.encheres.bll.UtilisateurService;
import fr.eni.encheres.bo.Adresse;
import fr.eni.encheres.bo.Role;
import fr.eni.encheres.bo.Utilisateur;
import fr.eni.encheres.dal.AdresseDAO;
import fr.eni.encheres.dal.UtilisateurDAO;
import fr.eni.encheres.dal.UtilisateurRoleDAO;
import fr.eni.encheres.exception.BusinessException;

@Service
public class UtilisateurServiceImpl implements UtilisateurService {

	private final UtilisateurDAO utilisateurDAO;
	//private final PasswordEncoder passwordEncoder;
	private final AdresseDAO adresseDAO;
	private final UtilisateurRoleDAO utilisateurRoleDAO;

	// -----Constructeur avec Spring Security-----

	/*
	 * public UtilisateurServiceImpl(UtilisateurDAO utilisateurDAO, AdresseDAO
	 * adresseDAO, UtilisateurRoleDAO utilisateurRoleDAO, PasswordEncoder
	 * passwordEncoder) { this.utilisateurDAO = utilisateurDAO; this.adresseDAO =
	 * adresseDAO; this.utilisateurRoleDAO = utilisateurRoleDAO;
	 * this.passwordEncoder = passwordEncoder; }
	 */
	public UtilisateurServiceImpl(UtilisateurDAO utilisateurDAO, AdresseDAO adresseDAO,
			UtilisateurRoleDAO utilisateurRoleDAO) {
		this.utilisateurDAO = utilisateurDAO;
		this.adresseDAO = adresseDAO;
		this.utilisateurRoleDAO = utilisateurRoleDAO;
	}

	@Override
	@Transactional
	public void creerUtilisateurAvecAdresseEtRole(Utilisateur utilisateur, Adresse adresse, Role role)
			throws BusinessException {
		BusinessException be = new BusinessException();

		if (!isEmailAvailable(utilisateur.getEmail(), be)) {
			throw be;
		}

		try {

			// Encodage du mot de passe avant insertion en base
			// String encodedPwd = passwordEncoder.encode(utilisateur.getMotDePasse());
			// utilisateur.setMotDePasse(encodedPwd);
			utilisateurDAO.insert(utilisateur);
			adresse.setIdUtilisateur(utilisateur.getIdUtilisateur());
			long idAdresse = adresseDAO.insert(adresse);
			adresse.setIdAdresse(idAdresse);

			utilisateurRoleDAO.insert(utilisateur.getIdUtilisateur(), role.getIdRole());

			utilisateur.getRoles().add(role);
			utilisateur.setAdresse(adresse);

		} catch (DataAccessException e) {
			be.add("Erreur d'accès à la base de données");
			throw be;
		}
	}

	private boolean isEmailAvailable(String email, BusinessException be) {
		boolean exists = utilisateurDAO.hasEmail(email);
		if (exists) {
			be.add("L'email existe déjà");
			return false;
		}
		return true;
	}

	@Override
	@Transactional
	public void modifierProfil(Utilisateur utilisateur) throws BusinessException {
		if (utilisateur == null || utilisateur.getIdUtilisateur() == 0) {
			throw new BusinessException("Utilisateur invalide");
		}
		try {
			utilisateurDAO.update(utilisateur);
			Adresse adresse = utilisateur.getAdresse();
			if (adresse != null) {
				adresse.setIdUtilisateur(utilisateur.getIdUtilisateur());
				if (adresse.getIdAdresse() == 0) {
					long idAdresse = adresseDAO.insert(adresse);
					adresse.setIdAdresse(idAdresse);
				} else {
					adresseDAO.update(adresse);
				}
			}
		} catch (DataAccessException e) {
			throw new BusinessException("Erreur lors de la mise à jour du profil utilisateur");
		}
	}

	@Override
	@Transactional
	public void supprimerCompte(long idUtilisateur) throws BusinessException {
		try {
			utilisateurRoleDAO.deleteByUserId(idUtilisateur);
			adresseDAO.deleteByUtilisateurId(idUtilisateur);
			utilisateurDAO.delete(idUtilisateur);
		} catch (DataAccessException e) {
			throw new BusinessException("Erreur lors de la suppression du compte utilisateur");
		}
	}

	private void chargerAdresse(Utilisateur utilisateur) {
		if (utilisateur != null) {
			Adresse adresse = adresseDAO.selectByUtilisateurId(utilisateur.getIdUtilisateur());
			utilisateur.setAdresse(adresse);
		}
	}

	@Override
	public Utilisateur getUtilisateurByEmail(String email) throws BusinessException {
		Utilisateur utilisateur = utilisateurDAO.selectByEmail(email);
		if (utilisateur == null) {
			BusinessException exception = new BusinessException();
			exception.add("Aucun utilisateur trouvé avec l'email : " + email);
			throw exception;
		}
		chargerAdresse(utilisateur);
		return utilisateur;
	}

	@Override
	public Utilisateur selectById(int idUtilisateur) {
		Utilisateur utilisateur = utilisateurDAO.selectById(idUtilisateur);
		chargerAdresse(utilisateur);
		return utilisateur;
	}

	@Override
	public Utilisateur selectByLogin(String login) {
		Utilisateur utilisateur = utilisateurDAO.selectByLogin(login);
		chargerAdresse(utilisateur);
		return utilisateur;
	}

	@Override
	public List<Utilisateur> selectAll() {
		return utilisateurDAO.selectAll(); // déjà avec adresse chargée car prevu dans UtilisateurDAOImpl Result
	}

}
