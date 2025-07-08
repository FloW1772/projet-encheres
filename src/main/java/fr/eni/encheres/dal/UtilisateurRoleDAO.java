package fr.eni.encheres.dal;

import java.util.List;

import fr.eni.encheres.bo.Role;

public interface UtilisateurRoleDAO {
	
    void deleteByUserId(long idUtilisateur);
    List<Role> findRoleByUserId(long idUtilisateur);
    List<Long> findUserIdsByRoleId(int idRole);
    void updateRoleForUser(long idUtilisateur, int oldIdRole, int newIdRole);
	void insert(long idUtilisateur, long idRole);
	void deleteRoleFromUser(long idUtilisateur, long idRole);
}
