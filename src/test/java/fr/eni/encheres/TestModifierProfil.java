package fr.eni.encheres;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import fr.eni.encheres.bll.UtilisateurService;
import fr.eni.encheres.bo.Adresse;
import fr.eni.encheres.bo.Role;
import fr.eni.encheres.bo.Utilisateur;
import fr.eni.encheres.exception.BusinessException;

@SpringBootTest
public class TestModifierProfil {

    @Autowired
    private UtilisateurService monService; 

    @Test
    public void testModifierProfil() throws BusinessException {
    	Utilisateur u = new Utilisateur();
    	
    	
    	if (u.getAdresse() == null) {
    	    u.setAdresse(new Adresse());
    	}

    	u.getAdresse().setRue("123 rue modifiée");
    	u.getAdresse().setCodePostal("75000");
    	u.getAdresse().setVille("Paris");
    	u.getAdresse().setPays("Paris");
    	u.setIdUtilisateur(1L);
    	u.setPseudo("alice123");
    	u.setNom("Dupont");         
    	u.setPrenom("Alice");       
    	u.setEmail("alice@mail.com");
    	u.setTelephone("0102030405");
    	u.setMotDePasse("pass123"); // merde il fallait pas
    	u.setCredit(100);
       
        List<Role> roles = new ArrayList<>();
      
        Role roleUtilisateur = new Role();
        roleUtilisateur.setIdRole(3);
        roleUtilisateur.setLibelle("UTILISATEUR");
        roles.add(roleUtilisateur);


        Role roleVendeur = new Role();
        roleVendeur.setIdRole(2);
        roleVendeur.setLibelle("VENDEUR");
        roles.add(roleVendeur);

  
        u.setRoles(roles);


        System.out.println("Avant appel modifierProfil");
        monService.modifierProfil(u);
        System.out.println("Après appel modifierProfil");
    }
}