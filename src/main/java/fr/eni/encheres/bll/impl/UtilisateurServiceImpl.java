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
import fr.eni.encheres.dal.RoleDAO;
import fr.eni.encheres.dal.UtilisateurDAO;
import fr.eni.encheres.dal.UtilisateurRoleDAO;
import fr.eni.encheres.exception.BusinessException;

@Service
public class UtilisateurServiceImpl implements UtilisateurService {

	private final UtilisateurDAO utilisateurDAO;
	// private final PasswordEncoder passwordEncoder;
	private final AdresseDAO adresseDAO;
	private final UtilisateurRoleDAO utilisateurRoleDAO;
	private final RoleDAO roleDAO;

	// -----Constructeur avec Spring Security-----

	/*
	 * public UtilisateurServiceImpl(UtilisateurDAO utilisateurDAO, AdresseDAO
	 * adresseDAO, UtilisateurRoleDAO utilisateurRoleDAO, PasswordEncoder
	 * passwordEncoder) { this.utilisateurDAO = utilisateurDAO; this.adresseDAO =
	 * adresseDAO; this.utilisateurRoleDAO = utilisateurRoleDAO;
	 * this.passwordEncoder = passwordEncoder; }
	 */

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void creerUtilisateurAvecAdresseEtRole(Utilisateur utilisateur, Adresse adresse, Role role)
			throws BusinessException {
		BusinessException be = new BusinessException();

		if (!isEmailAvailable(utilisateur.getEmail(), be)) {
			throw be;
		}

		try {
			// Encodage du mot de passe avant insertion en base (si nécessaire)
			// String encodedPwd = passwordEncoder.encode(utilisateur.getMotDePasse());
			// utilisateur.setMotDePasse(encodedPwd);

			System.out.println(">>> Insertion utilisateur");
			utilisateurDAO.insert(utilisateur);
			System.out.println(">>> ID utilisateur généré : " + utilisateur.getIdUtilisateur());

			if (adresse != null) {
				// On lie l'adresse à l'utilisateur
				adresse.setIdUtilisateur(utilisateur.getIdUtilisateur());
				System.out.println(">>> Insertion adresse");
				adresseDAO.save(adresse);
				utilisateur.setAdresse(adresse);

				// Mettre à jour l'utilisateur avec l'idAdresse
				System.out.println(">>> Mise à jour de l'utilisateur avec idAdresse");
				utilisateurDAO.updateIdAdresse(utilisateur.getIdUtilisateur(), adresse.getIdAdresse());
			} else {
				System.out.println(">>> Pas d'adresse fournie, insertion ignorée");
			}

			System.out.println(">>> Insertion rôle utilisateur");
			utilisateurRoleDAO.insert(utilisateur.getIdUtilisateur(), role.getIdRole());

		} catch (DataAccessException e) {
			e.printStackTrace();
			be.add("Erreur d'accès à la base de données");
			throw be;
		}
	}

	public UtilisateurServiceImpl(UtilisateurDAO utilisateurDAO, AdresseDAO adresseDAO,
			UtilisateurRoleDAO utilisateurRoleDAO, RoleDAO roleDAO) {
		super();
		this.utilisateurDAO = utilisateurDAO;
		this.adresseDAO = adresseDAO;
		this.utilisateurRoleDAO = utilisateurRoleDAO;
		this.roleDAO = roleDAO;
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
					adresseDAO.save(adresse);
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
			adresseDAO.delete(idUtilisateur);
			utilisateurDAO.delete(idUtilisateur);
		} catch (DataAccessException e) {
			throw new BusinessException("Erreur lors de la suppression du compte utilisateur");
		}
	}

	private void chargerAdresse(Utilisateur utilisateur) {
		if (utilisateur != null) {
			Adresse adresse = adresseDAO.selectAllByUtilisateurId(utilisateur.getIdUtilisateur());
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
	public Utilisateur selectById(long idUtilisateur) {
		Utilisateur utilisateur = utilisateurDAO.selectById(idUtilisateur);
		chargerAdresse(utilisateur);
		return utilisateur;
	}

	@Override
	public Utilisateur selectByLogin(String pseudo) throws BusinessException {
		Utilisateur utilisateur = null;
		try {
			utilisateur = utilisateurDAO.selectByLogin(pseudo);
			if (utilisateur != null) {
				Adresse adresse = adresseDAO.selectAllByUtilisateurId(utilisateur.getIdUtilisateur());
				utilisateur.setAdresse(adresse);
			}
		} catch (DataAccessException e) {
			BusinessException be = new BusinessException();
			be.add("Erreur lors de la récupération de l'utilisateur");
			throw be;
		}
		return utilisateur;
	}

	@Override
	public List<Utilisateur> selectAll() {
		return utilisateurDAO.selectAll(); // déjà avec adresse chargée car prevu dans UtilisateurDAOImpl Result
	}

	@Override
	@Transactional
	public void debiterPoints(long idUtilisateur, int montant) throws BusinessException {
		try {
			utilisateurDAO.debiterPoints(idUtilisateur, montant);
		} catch (RuntimeException e) {
			BusinessException be = new BusinessException("Impossible de débiter les points");
			be.add(e.getMessage());
			throw be;
		}
	}

	@Override
	@Transactional
	public void crediterPoints(long idUtilisateur, int montant) throws BusinessException {
		try {
			utilisateurDAO.crediterPoints(idUtilisateur, montant);
		} catch (RuntimeException e) {
			BusinessException be = new BusinessException("Impossible de créditer les points");
			be.add(e.getMessage());
			throw be;
		}
	}

	@Override
	@Transactional
	public void devenirVendeur(long idUtilisateur) throws BusinessException {
		try {

			Role roleVendeur = roleDAO.selectByLibelle("VENDEUR");
			if (roleVendeur == null) {
				throw new BusinessException("Le rôle VENDEUR n'existe pas");
			}

			List<Role> rolesExistants = utilisateurRoleDAO.findRoleIdsByUserId(idUtilisateur);
			boolean dejaVendeur = rolesExistants.stream().anyMatch(r -> "VENDEUR".equalsIgnoreCase(r.getLibelle()));

			if (dejaVendeur) {
				throw new BusinessException("L'utilisateur est déjà vendeur");
			}
			utilisateurRoleDAO.insert(idUtilisateur, roleVendeur.getIdRole());

		} catch (DataAccessException e) {
			throw new BusinessException("Erreur lors de l'attribution du rôle VENDEUR");
		}
	}

}
