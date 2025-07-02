package fr.eni.encheres.dal;

import java.util.List;

import fr.eni.encheres.bo.Role;
import fr.eni.encheres.exception.BusinessException;

public interface RoleDAO {
	
	  	List<Role> selectAll() ;
	    Role selectById(int idRole) ;
	    Role selectByLibelle(String libelle) ;
	    void insert(Role role) ;
	    void update(Role role) ;
	    void delete(int idRole) ;
}
