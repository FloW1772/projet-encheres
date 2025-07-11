package fr.eni.encheres.configuration;

 
	import jakarta.servlet.MultipartConfigElement;
	import org.springframework.boot.web.servlet.MultipartConfigFactory;

	import org.springframework.context.annotation.Bean;

	import org.springframework.context.annotation.Configuration;

	import org.springframework.util.unit.DataSize;
	 
	@Configuration

	public class MultipartConfig {
	 
	    @Bean

	    public MultipartConfigElement multipartConfigElement() {

	        MultipartConfigFactory factory = new MultipartConfigFactory();

	        factory.setMaxFileSize(DataSize.ofMegabytes(10)); // max taille par fichier

	        factory.setMaxRequestSize(DataSize.ofMegabytes(50)); // max taille totale de la requête
	 
	        // Le dossier où les fichiers uploadés seront temporairement stockés

	        factory.setLocation("images");
	 
	        return factory.createMultipartConfig();

	    }

	}
	 