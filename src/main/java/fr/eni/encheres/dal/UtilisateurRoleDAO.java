package fr.eni.encheres.dal;

import java.util.List;

import fr.eni.encheres.bo.Role;

public interface UtilisateurRoleDAO {
	
    void deleteByUserId(long idUtilisateur);
    List<Role> findRoleIdsByUserId(long idUtilisateur);
    List<Long> findUserIdsByRoleId(int idRole);
    void updateRoleForUser(long idUtilisateur, int oldIdRole, int newIdRole);
	void insert(long idUtilisateur, long idRole);
}
