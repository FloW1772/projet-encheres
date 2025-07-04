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
	public String profilUtilisateur(@RequestParam(name = "pseudo") String pseudo, Model model) {
		Utilisateur utilisateur = this.utilisateurService.selectByLogin(pseudo);
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

	@PostMapping("/utilisateur/creer")
	public String creerUtilisateur(
	        @Valid @ModelAttribute("utilisateur") Utilisateur utilisateur,
	        BindingResult bindingResult,
	        @RequestParam("confirmationMotDePasse") String confirmationMotDePasse,
	        Model model
	) {
	    // Vérifie les erreurs de validation des annotations
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
	        utilisateurService.creerUtilisateurAvecAdresseEtRole(
	                utilisateur,
	                utilisateur.getAdresse(),
	                utilisateur.getRole()
	        );
	        return "redirect:/view-login";
	    } catch (BusinessException e) {
	        model.addAttribute("roles", roleService.getAllRoles());
	        model.addAttribute("errors", e.getMessages());
	        return "view-inscription";
	    }
	}

	//-------- gesition à la mano-----
	    @GetMapping("/login")
	    public String loginForm() {
	        return "view-login"; 
	    }

	    @PostMapping("/login")
	    public String login(@RequestParam(name= "pseudo") String pseudo, HttpSession session, Model model) {
	        // Ici tu devrais valider le pseudo avec la base, je fais simple
	        if (pseudo != null && !pseudo.isEmpty()) {
	            Utilisateur utilisateur = new Utilisateur();
	            utilisateur.setIdUtilisateur(1L); // id fictif
	            utilisateur.setPseudo(pseudo);
	            session.setAttribute("utilisateurConnecte", utilisateur);
	            return "redirect:/encheres";
	        }
	        model.addAttribute("messageErreur", "Pseudo invalide");
	        return "view-login";
	    }

	    @GetMapping("/logout")
	    public String logout(HttpSession session) {
	        session.invalidate();
	        return "redirect:/login";
	    }
	

	
}
