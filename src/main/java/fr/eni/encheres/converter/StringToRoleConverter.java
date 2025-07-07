package fr.eni.encheres.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import fr.eni.encheres.bll.RoleService;
import fr.eni.encheres.bo.Role;
import fr.eni.encheres.dal.RoleDAO;

@Component
public class StringToRoleConverter implements Converter<String, Role> {

	private final RoleDAO roleDAO;

    public StringToRoleConverter(RoleDAO roleDAO) {
        this.roleDAO = roleDAO;
    }

    @Override
    public Role convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        try {
            int idRole = Integer.parseInt(source);
            return roleDAO.selectById(idRole);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}