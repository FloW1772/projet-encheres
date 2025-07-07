package fr.eni.encheres.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
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
	private final PasswordEncoder passwordEncoder;

	public UtilisateurController(UtilisateurService utilisateurService, RoleService roleService,
			PasswordEncoder passwordEncoder) {
		this.utilisateurService = utilisateurService;
		this.roleService = roleService;
		this.passwordEncoder = passwordEncoder;
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
		Role roleUtilisateur = roleService.getRoleByLibelle("UTILISATEUR");
		utilisateur.setRoles(new ArrayList<>(List.of(roleUtilisateur)));
		model.addAttribute("utilisateur", utilisateur);
		model.addAttribute("roles", roleService.getAllRoles());
		return "view-inscription";
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
			// Encodage du mot de passe avant la sauvegarde
			utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));

			// Passe la liste des rôles ici
			utilisateurService.creerUtilisateurAvecAdresseEtRoles(utilisateur, utilisateur.getAdresse(),
					utilisateur.getRoles());
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

	/// --------changer popur la mep

	@GetMapping("/utilisateur/modifier")
	public String afficherFormulaireModification(Principal principal, Model model) throws BusinessException {
		if (principal == null) {
			return "redirect:/login";
		}

		// Récupération du pseudo connecté
		String pseudo = principal.getName();

		Utilisateur utilisateur = utilisateurService.selectByLogin(pseudo);
		if (utilisateur == null) {
			return "redirect:/login";
		}

		if (utilisateur.getAdresse() == null) {
			utilisateur.setAdresse(new Adresse());
		}

		// Protection contre rôle null
		if (utilisateur.getRoles() == null || utilisateur.getRoles().isEmpty()) {
			Role roleParDefaut = new Role();
			roleParDefaut.setLibelle("UTILISATEUR");
			utilisateur.setRoles(List.of(roleParDefaut));
		}

		// Vérifie si l'utilisateur est simple utilisateur (sans autre rôle)
		boolean estSimpleUtilisateur = utilisateur.getRoles().stream()
				.anyMatch(r -> "UTILISATEUR".equalsIgnoreCase(r.getLibelle()));

		model.addAttribute("utilisateur", utilisateur);
		model.addAttribute("estSimpleUtilisateur", estSimpleUtilisateur);

		return "view-utilisateur";
	}

	@PostMapping("/utilisateur/modifier")
	public String modifierUtilisateur(@Valid @ModelAttribute("utilisateur") Utilisateur utilisateur,
			BindingResult result, @RequestParam(name = "action", required = false) String action, Principal principal,
			Model model) throws BusinessException {

		if (principal == null) {
			return "redirect:/login";
		}

		if (result.hasErrors()) {
			boolean estSimpleUtilisateur = utilisateur.getRoles() != null
					&& utilisateur.getRoles().stream().anyMatch(r -> "UTILISATEUR".equalsIgnoreCase(r.getLibelle()));

			model.addAttribute("estSimpleUtilisateur", estSimpleUtilisateur);
			return "view-utilisateur";
		}

		try {
			if ("devenirVendeur".equals(action)) {
				utilisateurService.devenirVendeur(utilisateur.getIdUtilisateur());
			}

			utilisateurService.modifierProfil(utilisateur);

			Utilisateur utilisateurMisAJour = utilisateurService.selectById(utilisateur.getIdUtilisateur());

			return "redirect:/profil?pseudo=" + utilisateurMisAJour.getPseudo();
		} catch (BusinessException e) {
			model.addAttribute("errors", e.getMessages());

			boolean estSimpleUtilisateur = utilisateur.getRoles() != null
					&& utilisateur.getRoles().stream().anyMatch(r -> "UTILISATEUR".equalsIgnoreCase(r.getLibelle()));

			model.addAttribute("estSimpleUtilisateur", estSimpleUtilisateur);
			return "view-modification";
		}
	}

}
