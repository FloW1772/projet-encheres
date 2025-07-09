package fr.eni.encheres.bll.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.eni.encheres.bll.UtilisateurService;
import fr.eni.encheres.bo.Adresse;
import fr.eni.encheres.bo.Enchere;
import fr.eni.encheres.bo.Role;
import fr.eni.encheres.bo.Utilisateur;
import fr.eni.encheres.configuration.PasswordEncoderConfig;
import fr.eni.encheres.dal.AdresseDAO;
import fr.eni.encheres.dal.RoleDAO;
import fr.eni.encheres.dal.UtilisateurDAO;
import fr.eni.encheres.dal.UtilisateurRoleDAO;
import fr.eni.encheres.exception.BusinessException;

@Service
public class UtilisateurServiceImpl implements UtilisateurService {

	private final UtilisateurDAO utilisateurDAO;
	 private final PasswordEncoder passwordEncoder;
	private final AdresseDAO adresseDAO;
	private final UtilisateurRoleDAO utilisateurRoleDAO;
	private final RoleDAO roleDAO;

	

	
	public UtilisateurServiceImpl(UtilisateurDAO utilisateurDAO, PasswordEncoder passwordEncoder,
			AdresseDAO adresseDAO, UtilisateurRoleDAO utilisateurRoleDAO, RoleDAO roleDAO) {
		super();
		this.utilisateurDAO = utilisateurDAO;
		this.passwordEncoder = passwordEncoder;
		this.adresseDAO = adresseDAO;
		this.utilisateurRoleDAO = utilisateurRoleDAO;
		this.roleDAO = roleDAO;
	}


	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void creerUtilisateurAvecAdresseEtRoles(Utilisateur utilisateur, Adresse adresse, List<Role> roles)
	        throws BusinessException {
	    BusinessException be = new BusinessException();

	    if (!isEmailAvailable(utilisateur.getEmail(), be)) {
	        throw be;
	    }

	    try {
	        // Encode le mot de passe
	        String rawPassword = utilisateur.getMotDePasse();
	        String encodedPassword = passwordEncoder.encode(rawPassword);
	        utilisateur.setMotDePasse(encodedPassword);

	        System.out.println(">>> Insertion utilisateur");
	        utilisateurDAO.insert(utilisateur);
	        System.out.println(">>> ID utilisateur généré : " + utilisateur.getIdUtilisateur());

	        if (adresse != null) {
	            adresse.setIdUtilisateur(utilisateur.getIdUtilisateur());
	            System.out.println(">>> Insertion adresse");
	            adresseDAO.save(adresse);
	            utilisateur.setAdresse(adresse);

	            System.out.println(">>> Mise à jour de l'utilisateur avec idAdresse");
	            utilisateurDAO.updateIdAdresse(utilisateur.getIdUtilisateur(), adresse.getIdAdresse());
	        } else {
	            System.out.println(">>> Pas d'adresse fournie, insertion ignorée");
	        }

	        if (roles == null) {
	            roles = new ArrayList<>();
	        } else {
	            // ----Nettoyer la liste des rôles pour ne garder que ceux non nulls
	            roles = roles.stream()
	                         .filter(Objects::nonNull)
	                         .collect(Collectors.toList());
	        }

	        Role roleUtilisateur = roleDAO.selectByLibelle("UTILISATEUR");
	        if (roleUtilisateur == null) {
	            throw new BusinessException("Le rôle UTILISATEUR par défaut n'existe pas");
	        }

	        boolean roleUtilisateurPresent = roles.stream()
	                .anyMatch(r -> "UTILISATEUR".equalsIgnoreCase(r.getLibelle()));

	        if (!roleUtilisateurPresent) {
	            roles.add(roleUtilisateur);
	        }

	        if (!roles.isEmpty()) {
	            System.out.println(">>> Insertion rôles utilisateur");
	            for (Role role : roles) {
	                utilisateurRoleDAO.insert(utilisateur.getIdUtilisateur(), role.getIdRole());
	            }
	        } else {
	            System.out.println(">>> Aucun rôle fourni, insertion ignorée");
	        }

	    } catch (DataAccessException e) {
	        e.printStackTrace();
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

	@Transactional
	public void modifierProfil(Utilisateur utilisateur) throws BusinessException {
	    System.out.println("Entrée dans modifierProfil");
	    if (utilisateur == null || utilisateur.getIdUtilisateur() == 0) {
	        throw new BusinessException("Utilisateur invalide");
	    }

	    try {
	        // Charger l'utilisateur complet depuis la BDD
	        Utilisateur utilisateurEnBase = utilisateurDAO.selectById(utilisateur.getIdUtilisateur());
	        if (utilisateurEnBase == null) {
	            throw new BusinessException("Utilisateur non trouvé");
	        }

	        // Mettre à jour les champs simples
	        utilisateurEnBase.setPseudo(utilisateur.getPseudo());
	        utilisateurEnBase.setNom(utilisateur.getNom());
	        utilisateurEnBase.setPrenom(utilisateur.getPrenom());
	        utilisateurEnBase.setEmail(utilisateur.getEmail());
	        utilisateurEnBase.setTelephone(utilisateur.getTelephone());
	      //  utilisateurEnBase.setCredit(utilisateur.getCredit());
	        
	        
	        if (utilisateur.getMotDePasse() != null && !utilisateur.getMotDePasse().isEmpty()) {
	            String motDePasseHashe = passwordEncoder.encode(utilisateur.getMotDePasse());
	            utilisateurEnBase.setMotDePasse(motDePasseHashe);
	        }


	        // Mettre à jour l'adresse 
	        Adresse adresseEnBase = utilisateurEnBase.getAdresse();
	        Adresse adresseModifiee = utilisateur.getAdresse();

	        if (adresseModifiee != null) {
	            if (adresseEnBase == null) {
	                // Pas d'adresse en base, on crée une nouvelle
	                adresseModifiee.setIdUtilisateur(utilisateur.getIdUtilisateur());
	                adresseDAO.save(adresseModifiee);
	                utilisateurEnBase.setAdresse(adresseModifiee);
	            } else {
	                // Mise à jour de l'adresse existante
	                adresseEnBase.setRue(adresseModifiee.getRue());
	                adresseEnBase.setCodePostal(adresseModifiee.getCodePostal());
	                adresseEnBase.setVille(adresseModifiee.getVille());
	                adresseEnBase.setPays(adresseModifiee.getPays());

	                adresseDAO.update(adresseEnBase);
	            }
	        }

	        //-------- Mise à jour de l'utilisateur
	        utilisateurDAO.update(utilisateurEnBase);

	        // ------Gestion des rôles
	    /*    List<Role> nouveauxRoles = utilisateur.getRoles();
	        if (nouveauxRoles != null) {
	            // Récupérer les rôles actuels depuis la BDD
	            List<Role> rolesExistants = utilisateurRoleDAO.findRoleByUserId(utilisateur.getIdUtilisateur());
	            System.out.println(rolesExistants);

	            // ------Ajouter les nouveaux rôles absents dans la base
	            for (Role roleNouveau : nouveauxRoles) {
	                boolean present = rolesExistants.stream()
	                        .anyMatch(r -> r.getIdRole() == roleNouveau.getIdRole());
	                if (!present) {
	                    utilisateurRoleDAO.insert(utilisateur.getIdUtilisateur(), roleNouveau.getIdRole());
	                }
	            }
	        }*/

	    } catch (DataAccessException e) {
	        e.printStackTrace();
	        throw new BusinessException("Erreur lors de la mise à jour du profil utilisateur");
	    }
	}


	@Override
	@Transactional
	public void supprimerCompte(long idUtilisateur) throws BusinessException {
	    try {
	        // ---Supprimer les rôles
	        utilisateurRoleDAO.deleteByUserId(idUtilisateur);

	        //--- Détacher l'adresse de l'utilisateur pour lever la contrainte FK
	        utilisateurDAO.clearAdresse(idUtilisateur); 

	        //--- Supprimer les adresses liées à l'utilisateur 
	        adresseDAO.delete(idUtilisateur);

	        // ---Supprimer l'utilisateur
	        utilisateurDAO.delete(idUtilisateur);
	    } catch (DataAccessException e) {
	        e.printStackTrace();
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

	            //  ---- récupération des rôles
	            List<Role> roles = utilisateurRoleDAO.findRoleByUserId(utilisateur.getIdUtilisateur());
	            utilisateur.setRoles(roles);
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

			List<Role> rolesExistants = utilisateurRoleDAO.findRoleByUserId(idUtilisateur);
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
