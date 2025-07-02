package fr.eni.encheres.dal;

import java.util.List;

public interface UtilisateurRoleDAO {
	
    void deleteByUserId(long idUtilisateur);
    List<Integer> findRoleIdsByUserId(long idUtilisateur);
    List<Long> findUserIdsByRoleId(int idRole);
    void updateRoleForUser(long idUtilisateur, int oldIdRole, int newIdRole);
	void insert(long idUtilisateur, long idRole);
}
