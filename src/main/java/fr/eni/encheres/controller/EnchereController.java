package fr.eni.encheres.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import fr.eni.encheres.bll.EnchereService;
import fr.eni.encheres.bo.Enchere;

@Controller
public class EnchereController {

    private final EnchereService enchereService;

    public EnchereController(EnchereService enchereService) {
        this.enchereService = enchereService;
    }

    @GetMapping("/encheres")
    public String afficherToutesLesEncheres(Model model) {
        List<Enchere> encheres = enchereService.readAll();
        model.addAttribute("encheres", encheres);
        return "view-liste-encheres";  
    }
}