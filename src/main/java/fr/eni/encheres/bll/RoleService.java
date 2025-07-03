package fr.eni.encheres.bll;

import java.util.List;

import fr.eni.encheres.bo.Role;

public interface RoleService {
	
    List<Role> getAllRoles();
    Role getRoleById(int idRole);

}
