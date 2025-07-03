package fr.eni.encheres.bll.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import fr.eni.encheres.bll.RoleService;
import fr.eni.encheres.bo.Role;
import fr.eni.encheres.dal.RoleDAO;

@Service
public class RoleServiceImpl implements RoleService {

	private final RoleDAO roleDAO;

	public RoleServiceImpl(RoleDAO roleDAO) {
		this.roleDAO = roleDAO;
	}

	@Override
	public List<Role> getAllRoles() {
		return roleDAO.selectAll();
	}

	@Override
	public Role getRoleById(int idRole) {
		return roleDAO.selectById(idRole);
	}

}
