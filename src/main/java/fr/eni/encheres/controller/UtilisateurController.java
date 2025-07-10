package fr.eni.encheres.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import fr.eni.encheres.bll.ArticleAVendreService;
import fr.eni.encheres.bll.EnchereService;
import fr.eni.encheres.bll.RoleService;
import fr.eni.encheres.bll.UtilisateurRoleService;
import fr.eni.encheres.bll.UtilisateurService;
import fr.eni.encheres.bo.Adresse;
import fr.eni.encheres.bo.ArticleAVendre;
import fr.eni.encheres.bo.Enchere;
import fr.eni.encheres.bo.Role;
import fr.eni.encheres.bo.Utilisateur;
import fr.eni.encheres.exception.BusinessException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class UtilisateurController {

	private UtilisateurService utilisateurService;
	private RoleService roleService;
	private ArticleAVendreService articleAVendreService;
	private UtilisateurRoleService utilisateurRoleService;
	private EnchereService enchereService;

	public UtilisateurController(UtilisateurService utilisateurService, RoleService roleService,
			ArticleAVendreService articleAVendreService, UtilisateurRoleService utilisateurRoleService,
			EnchereService enchereService) {
		this.utilisateurService = utilisateurService;
		this.roleService = roleService;
		this.articleAVendreService = articleAVendreService;
		this.utilisateurRoleService = utilisateurRoleService;
		this.enchereService = enchereService;

	}

	// ------- Connexion---------------
	@GetMapping("/login")
	public String loginForm() {
		return "view-login";
	}
//-------Error en cas de decnnexion--------

	@GetMapping("/logout-error")
	public String logoutError(Model model) {
		model.addAttribute("errorMessage", "Vous n'êtes pas connecté.");
		return "error";
	}

	// --------- Consultation profil si connecté alors le bouton modifier
	// apparait-----------
	@GetMapping("/utilisateur/profil")
	public String profilUtilisateur(@RequestParam(name = "pseudo") String pseudo, Model model, Principal principal)
			throws BusinessException {
		Utilisateur utilisateur = this.utilisateurService.selectByLogin(pseudo);
		if (utilisateur == null) {
			throw new BusinessException("Utilisateur introuvable");
		}

		if (utilisateur.getAdresse() == null) {
			utilisateur.setAdresse(new Adresse());
		}
		model.addAttribute("utilisateur", utilisateur);

		// --------Ajouter l'utilisateur connecté au modèle
		if (principal != null) {
			Utilisateur utilisateurConnecte = this.utilisateurService.selectByLogin(principal.getName());
			model.addAttribute("utilisateurConnecte", utilisateurConnecte);
		}

		return "view-profil";
	}

	@GetMapping("/utilisateur/creer")
	public String showInscriptionForm(Model model) {
		Utilisateur utilisateur = new Utilisateur();
		utilisateur.setAdresse(new Adresse());
		//------- Refactor Creation compte-------
		//Role roleUtilisateur = roleService.getRoleByLibelle("UTILISATEUR");
		//utilisateur.setRoles(new ArrayList<>(List.of(roleUtilisateur)));
		//model.addAttribute("roles", roleService.getAllRoles());
		model.addAttribute("utilisateur", utilisateur);
		return "view-inscription";
	}

	public List<Role> getAllRoles() {
		List<Role> roles = roleService.getAllRoles();
		roles.forEach(role -> System.out.println("Role dispo: " + role.getLibelle()));
		return roles;
	}

	@PostMapping("/utilisateur/creer")
	public String creerUtilisateur(@Valid @ModelAttribute("utilisateur") Utilisateur utilisateur,
			BindingResult bindingResult, @RequestParam("confirmationMotDePasse") String confirmationMotDePasse,
			Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("roles", roleService.getAllRoles());
			return "view-inscription";
		}

		if (!utilisateur.getMotDePasse().equals(confirmationMotDePasse)) {
			bindingResult.rejectValue("motDePasse", "error.motDePasse", "Les mots de passe ne correspondent pas");
			model.addAttribute("roles", roleService.getAllRoles());
			model.addAttribute("confirmationMotDePasseErreur", "Les mots de passe ne correspondent pas");
			return "view-inscription";
		}
		try {
			
			//------- Refactor Creation compte Toujours utilisateur-------
			/*utilisateurService.creerUtilisateurAvecAdresseEtRoles(utilisateur, utilisateur.getAdresse(),
					utilisateur.getRoles());*/
			
			utilisateurService.creerUtilisateurAvecAdresseEtRoles(utilisateur, utilisateur.getAdresse(), null);


			return "redirect:/login";

		} catch (BusinessException e) {

			model.addAttribute("roles", roleService.getAllRoles());
			model.addAttribute("errors", e.getMessages());
			return "view-inscription";
		}
	}

	@GetMapping("/utilisateur/modifier")
	public String afficherFormulaireModification(Principal principal, Model model) throws BusinessException {
		if (principal == null) {
			return "redirect:/login";
		}

		String pseudo = principal.getName();
		Utilisateur utilisateur = utilisateurService.selectByLogin(pseudo);

		if (utilisateur == null) {
			return "redirect:/login";
		}

		if (utilisateur.getAdresse() == null) {
			utilisateur.setAdresse(new Adresse());
		}

		// ------Récupérer les rôles de cet utilisateur
		List<Role> rolesUtilisateur = utilisateurRoleService
				.recupererRolesParUtilisateur(utilisateur.getIdUtilisateur());
		utilisateur.setRoles(rolesUtilisateur);

		// S’il n’a pas de rôle, on lui met le rôle UTILISATEUR par défaut
		if (rolesUtilisateur == null || rolesUtilisateur.isEmpty()) {
			Role roleParDefaut = new Role();
			roleParDefaut.setLibelle("UTILISATEUR");
			utilisateur.setRoles(List.of(roleParDefaut));
		}

		if (utilisateur.getMotDePasse() != null && !utilisateur.getMotDePasse().isEmpty()) {
			utilisateur.setMotDePasse("*****");
		}

		// -----------Vérifier si l'utilisateur est juste simple utilisateur
		boolean estSimpleUtilisateur = utilisateur.getRoles().size() == 1
				&& "UTILISATEUR".equalsIgnoreCase(utilisateur.getRoles().get(0).getLibelle());

		model.addAttribute("utilisateur", utilisateur);
		model.addAttribute("estSimpleUtilisateur", estSimpleUtilisateur);

		return "view-utilisateur";
	}

	@PostMapping("/utilisateur/modifier")
	public String modifierUtilisateur(@Valid @ModelAttribute("utilisateur") Utilisateur utilisateur,
			BindingResult result, @RequestParam(name = "action", required = false) String action,
			@RequestParam(name = "confirmationMotDePasse", required = false) String confirmationMotDePasse,
			Principal principal, Model model) throws BusinessException {

		if (principal == null) {
			return "redirect:/login";
		}

		// -------------Récupérer l'utilisateur actuel en base
		Utilisateur utilisateurEnBase = utilisateurService.selectById(utilisateur.getIdUtilisateur());

		// ---------Forcer la valeur du crédit pour qu’elle ne soit jamais modifiée par
		// le formulaire
		utilisateur.setCredit(utilisateurEnBase.getCredit());

		// --------Recherger les rôles
		List<Role> roles = utilisateurRoleService.recupererRolesParUtilisateur(utilisateur.getIdUtilisateur());
		// utilisateur.setRoles(roles);

		// ------- Gestion du mot de passe
		String mdpForm = utilisateur.getMotDePasse();
		System.out.println(mdpForm);
		// --------- Si vide ou non modifié, on ignore
		if (mdpForm == null || mdpForm.isEmpty()) {
			utilisateur.setMotDePasse(null); // On signale au service de ne pas modifier
		} else {
			// ------- Vérification longueur
			if (mdpForm.length() < 8) {
				model.addAttribute("confirmationMotDePasseError",
						"Le mot de passe doit contenir au moins 8 caractères.");
				boolean estSimpleUtilisateur = roles.size() == 1
						&& "UTILISATEUR".equalsIgnoreCase(roles.get(0).getLibelle());
				model.addAttribute("estSimpleUtilisateur", estSimpleUtilisateur);
				model.addAttribute("utilisateur", utilisateur);
				return "view-utilisateur";
			}

			// ----------- Vérification de confirmation
			if (confirmationMotDePasse == null || !confirmationMotDePasse.equals(mdpForm)) {
				model.addAttribute("confirmationMotDePasseError", "La confirmation ne correspond pas au mot de passe.");
				boolean estSimpleUtilisateur = roles.size() == 1
						&& "UTILISATEUR".equalsIgnoreCase(roles.get(0).getLibelle());
				model.addAttribute("estSimpleUtilisateur", estSimpleUtilisateur);
				model.addAttribute("utilisateur", utilisateur);
				return "view-utilisateur";
			}

		}

		try {
			if ("devenirVendeur".equals(action)) {
				utilisateurService.devenirVendeur(utilisateur.getIdUtilisateur());

				utilisateur = utilisateurService.selectById(utilisateur.getIdUtilisateur());
				List<Role> rolesMisAJour = utilisateurRoleService
						.recupererRolesParUtilisateur(utilisateur.getIdUtilisateur());
				utilisateur.setRoles(rolesMisAJour);
			} else {
				utilisateurService.modifierProfil(utilisateur);

				utilisateur = utilisateurService.selectById(utilisateur.getIdUtilisateur());
			}

			return "redirect:/utilisateur/profil?pseudo=" + utilisateur.getPseudo();

		} catch (BusinessException e) {
			model.addAttribute("errors", e.getMessages());

			boolean estSimpleUtilisateur = utilisateur.getRoles() != null && utilisateur.getRoles().size() == 1
					&& "UTILISATEUR".equalsIgnoreCase(utilisateur.getRoles().get(0).getLibelle());
			utilisateur.setRoles(roles);
			model.addAttribute("estSimpleUtilisateur", estSimpleUtilisateur);
			model.addAttribute("utilisateur", utilisateur);
			return "view-utilisateur";
		}
	}

	@PostMapping("/utilisateur/supprimer")
	public String supprimerUtilisateur(Principal principal, Model model) {
	    if (principal == null) {
	        return "redirect:/login";
	    }

	    Utilisateur utilisateur = null;
	    try {
	        String pseudo = principal.getName();
	        utilisateur = utilisateurService.selectByLogin(pseudo);

	        List<ArticleAVendre> articlesEnCours = articleAVendreService.findArticlesEnCoursByVendeur(pseudo);
	        List<Enchere> encheresEnCours = enchereService.readAll();

	        long idUtilisateur = utilisateur.getIdUtilisateur();
	        List<Enchere> encheresUtilisateur = encheresEnCours.stream()
	                .filter(e -> e.getEncherisseur() != null && e.getEncherisseur().getIdUtilisateur() == idUtilisateur)
	                .collect(Collectors.toList());

	        if (!articlesEnCours.isEmpty() || !encheresUtilisateur.isEmpty()) {
	            throw new BusinessException("Vous ne pouvez pas supprimer votre compte car vous avez des articles ou des enchères en cours.");
	        }

	        utilisateurService.supprimerCompte(idUtilisateur);
	        return "redirect:/encheres";

	    } catch (BusinessException e) {
	        model.addAttribute("errors", List.of(e.getMessage()));

	        if (utilisateur != null) {
	            model.addAttribute("utilisateur", utilisateur);
	            boolean estSimpleUtilisateur = utilisateur.getRoles().size() == 1 &&
	                "UTILISATEUR".equalsIgnoreCase(utilisateur.getRoles().get(0).getLibelle());
	            model.addAttribute("estSimpleUtilisateur", estSimpleUtilisateur);
	        }

	        
	        return "view-utilisateur";
	    }
	}

}