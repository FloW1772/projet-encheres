package fr.eni.encheres.bll;

import java.util.List;

import fr.eni.encheres.bo.Role;

public interface UtilisateurRoleService {

    void ajouterRoleUtilisateur(long idUtilisateur, long idRole);

    void supprimerRolesUtilisateur(long idUtilisateur);

    List<Role> recupererRolesParUtilisateur(long idUtilisateur);

    List<Long> recupererUtilisateursParRole(int idRole);

    void modifierRoleUtilisateur(long idUtilisateur, int oldIdRole, int newIdRole);

    void supprimerRoleUtilisateur(long idUtilisateur, long idRole);
}
