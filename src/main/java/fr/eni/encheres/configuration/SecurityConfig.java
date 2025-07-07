package fr.eni.encheres.configuration;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	  //@Autowired
     // private UserDetailsService userDetailsService;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/vente/**").hasRole("VENDEUR")
                .requestMatchers("/profil/**").hasAnyRole("UTILISATEUR", "VENDEUR", "ADMIN")
                .requestMatchers("/encheres").permitAll()
				.anyRequest().denyAll()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
            )
            .rememberMe(remember -> remember
                .tokenValiditySeconds(7 * 24 * 60 * 60)
                .key("maCleSecretePourRememberMe")
                .userDetailsService(users(null)) 
            );

        return http.build();
    }

    @Bean
    public UserDetailsManager users(DataSource dataSource) {
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);

        users.setUsersByUsernameQuery(
            "SELECT pseudo, motDePasse, 1 actif FROM UTILISATEUR WHERE pseudo = ?"
        );

        // Ajoute automatiquement "ROLE_" devant le statut pour correspondre à Spring
        users.setAuthoritiesByUsernameQuery(
        	    "SELECT u.pseudo AS username, CONCAT('ROLE_', r.libelle) AS authority " +
        	    "FROM Utilisateur u " +
        	    "JOIN Utilisateur_Role ur ON u.idUtilisateur = ur.idUtilisateur " +
        	    "JOIN Role r ON ur.idRole = r.idRole " +
        	    "WHERE u.pseudo = ?"
        	);
        return users;
    }
    
}


