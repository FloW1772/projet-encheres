package fr.eni.encheres.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import fr.eni.encheres.bll.RoleService;
import fr.eni.encheres.bll.UtilisateurService;
import fr.eni.encheres.bo.Adresse;
import fr.eni.encheres.bo.Role;
import fr.eni.encheres.bo.Utilisateur;
import fr.eni.encheres.exception.BusinessException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class UtilisateurController {

	private UtilisateurService utilisateurService;
	private RoleService roleService;

	public UtilisateurController(UtilisateurService utilisateurService, RoleService roleService) {
		this.utilisateurService = utilisateurService;
		this.roleService = roleService;
	}

	@GetMapping("/utilisateur/profil")
	public String profilUtilisateur(@RequestParam(name = "pseudo") String pseudo, Model model)
			throws BusinessException {
		Utilisateur utilisateur = this.utilisateurService.selectByLogin(pseudo);
		if (utilisateur == null) {
			throw new BusinessException("Utilisateur introuvable");
		}

		if (utilisateur.getAdresse() == null) {
			utilisateur.setAdresse(new Adresse());
		}
		model.addAttribute("utilisateur", utilisateur);
		return "view-profil";
	}

	@GetMapping("/utilisateur/creer")
	public String showInscriptionForm(Model model) {
		Utilisateur utilisateur = new Utilisateur();
		utilisateur.setAdresse(new Adresse());
		utilisateur.getRole();
		model.addAttribute("utilisateur", utilisateur);
		model.addAttribute("roles", roleService.getAllRoles());
		return "view-inscription";
	}

	
	//----------
	@PostMapping("/utilisateur/creer")
	public String creerUtilisateur(@Valid @ModelAttribute("utilisateur") Utilisateur utilisateur,
			BindingResult bindingResult, @RequestParam("confirmationMotDePasse") String confirmationMotDePasse,
			Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("roles", roleService.getAllRoles());
			return "view-inscription";
		}

		// Vérifie si les deux mots de passe sont égaux
		if (!utilisateur.getMotDePasse().equals(confirmationMotDePasse)) {
			bindingResult.rejectValue("motDePasse", "error.motDePasse", "Les mots de passe ne correspondent pas");
			model.addAttribute("roles", roleService.getAllRoles());
			model.addAttribute("confirmationMotDePasseErreur", "Les mots de passe ne correspondent pas");
			return "view-inscription";
		}

		try {
			utilisateurService.creerUtilisateurAvecAdresseEtRole(utilisateur, utilisateur.getAdresse(),
					utilisateur.getRole());
			return "redirect:/login";
		} catch (BusinessException e) {
			model.addAttribute("roles", roleService.getAllRoles());
			model.addAttribute("errors", e.getMessages());
			return "view-inscription";
		}
	}

	// -------- gesition à la mano-----
	@GetMapping("/login")
	public String loginForm() {
		return "view-login";
	}
	
	
	///--------changer popur la mep

	@PostMapping("/login")
	public String login(@RequestParam(name = "pseudo") String pseudo, HttpSession session, Model model) {
		if (pseudo == null || pseudo.isEmpty()) {
			model.addAttribute("messageErreur", "Pseudo invalide");
			return "view-login";
		}
		try {
			Utilisateur utilisateur = utilisateurService.selectByLogin(pseudo); 
			if (utilisateur != null) {
				session.setAttribute("utilisateurConnecte", utilisateur);
				return "redirect:/encheres";
			} else {
				model.addAttribute("messageErreur", "Pseudo inconnu");
				return "view-login";
			}
		} catch (BusinessException e) {
			model.addAttribute("messageErreur", "Erreur lors de la connexion : " + String.join(", ", e.getMessages()));
			return "view-login";
		}
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/encheres";
	}
	
	
	
	@GetMapping("/utilisateur/modifier")
	public String afficherFormulaireModification(HttpSession session, Model model) throws BusinessException {
	    Utilisateur utilisateurConnecte = (Utilisateur) session.getAttribute("utilisateurConnecte");

	    if (utilisateurConnecte == null) {
	        return "redirect:/login";
	    }

	    Utilisateur utilisateur = utilisateurService.selectById(utilisateurConnecte.getIdUtilisateur());

	    if (utilisateur.getAdresse() == null) {
	        utilisateur.setAdresse(new Adresse());
	    }

	    // Protection contre un role null
	    if (utilisateur.getRole() == null) {
	        Role roleParDefaut = new Role();
	        roleParDefaut.setLibelle("UTILISATEUR");
	        utilisateur.setRole(roleParDefaut);
	    }

	    boolean estSimpleUtilisateur = "UTILISATEUR".equalsIgnoreCase(utilisateur.getRole().getLibelle());

	    model.addAttribute("utilisateur", utilisateur);
	    model.addAttribute("estSimpleUtilisateur", estSimpleUtilisateur);

	    return "view-utilisateur";
	}


	@PostMapping("/utilisateur/modifier")
	public String modifierUtilisateur(
	        @Valid @ModelAttribute("utilisateur") Utilisateur utilisateur,
	        BindingResult result,
	        @RequestParam(name = "action", required = false) String action,
	        HttpSession session,
	        Model model) {

	    Utilisateur utilisateurSession = (Utilisateur) session.getAttribute("utilisateurConnecte");

	    if (utilisateurSession == null) {
	        return "redirect:/login";
	    }

	    if (result.hasErrors()) {
	        boolean estSimpleUtilisateur = utilisateur.getRole() != null &&
	                "UTILISATEUR".equalsIgnoreCase(utilisateur.getRole().getLibelle());

	        model.addAttribute("estSimpleUtilisateur", estSimpleUtilisateur);
	        return "view-modification";
	    }

	    try {
	        if ("devenirVendeur".equals(action)) {
	            utilisateurService.devenirVendeur(utilisateur.getIdUtilisateur());
	        }

	        utilisateurService.modifierProfil(utilisateur);

	        Utilisateur utilisateurMisAJour = utilisateurService.selectById(utilisateur.getIdUtilisateur());
	        session.setAttribute("utilisateurConnecte", utilisateurMisAJour);

	        return "redirect:/profil";
	    } catch (BusinessException e) {
	        model.addAttribute("errors", e.getMessages());

	        boolean estSimpleUtilisateur = utilisateur.getRole() != null &&
	                "UTILISATEUR".equalsIgnoreCase(utilisateur.getRole().getLibelle());

	        model.addAttribute("estSimpleUtilisateur", estSimpleUtilisateur);
	        return "view-modification";
	    }
	}

}
