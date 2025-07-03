package fr.eni.encheres.bll.impl;

import org.springframework.stereotype.Service;

import fr.eni.encheres.bll.AdresseService;
import fr.eni.encheres.bo.Adresse;
import fr.eni.encheres.dal.AdresseDAO;

@Service
public class AdresseServiceImpl implements AdresseService {
	
	

    private final AdresseDAO adresseDAO;

    public AdresseServiceImpl(AdresseDAO adresseDAO) {
        this.adresseDAO = adresseDAO;
    }

    @Override
    public void save(Adresse adresse) {
        adresseDAO.save(adresse);
    }

    @Override
    public Adresse findById(long id) {
        return adresseDAO.findById(id);
    }

    @Override
    public Adresse selectAllByUtilisateurId(long idUtilisateur) {
        return adresseDAO.selectAllByUtilisateurId(idUtilisateur);
    }

    @Override
    public void update(Adresse adresse) {
        adresseDAO.update(adresse);
    }

    @Override
    public void delete(long id) {
        adresseDAO.delete(id);
    }
	
	
	

}
