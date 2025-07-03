package fr.eni.encheres.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import fr.eni.encheres.bo.ArticleAVendre;

@Component
public class StringToEtatVenteConverter implements Converter<String, ArticleAVendre.EtatVente> {

    @Override
    public ArticleAVendre.EtatVente convert(String source) {
        try {
            return ArticleAVendre.EtatVente.valueOf(source);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null; 
        }
    }
}
