package fr.eni.encheres.bll.impl;

import fr.eni.encheres.bll.UtilisateurRoleService;
import fr.eni.encheres.bo.Role;
import fr.eni.encheres.dal.UtilisateurRoleDAO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UtilisateurRoleServiceImpl implements UtilisateurRoleService {

    private final UtilisateurRoleDAO utilisateurRoleDAO;

    public UtilisateurRoleServiceImpl(UtilisateurRoleDAO utilisateurRoleDAO) {
        this.utilisateurRoleDAO = utilisateurRoleDAO;
    }

    @Override
    public void ajouterRoleUtilisateur(long idUtilisateur, long idRole) {
        utilisateurRoleDAO.insert(idUtilisateur, idRole);
    }

    @Override
    public void supprimerRolesUtilisateur(long idUtilisateur) {
        utilisateurRoleDAO.deleteByUserId(idUtilisateur);
    }

    @Override
    public List<Role> recupererRolesParUtilisateur(long idUtilisateur) {
        return utilisateurRoleDAO.findRoleByUserId(idUtilisateur);
    }

    @Override
    public List<Long> recupererUtilisateursParRole(int idRole) {
        return utilisateurRoleDAO.findUserIdsByRoleId(idRole);
    }

    @Override
    public void modifierRoleUtilisateur(long idUtilisateur, int oldIdRole, int newIdRole) {
        utilisateurRoleDAO.updateRoleForUser(idUtilisateur, oldIdRole, newIdRole);
    }

    @Override
    public void supprimerRoleUtilisateur(long idUtilisateur, long idRole) {
        utilisateurRoleDAO.deleteRoleFromUser(idUtilisateur, idRole);
    }
}
