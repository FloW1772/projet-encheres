package fr.eni.encheres.configuration;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserDetailsManager userDetailsManager) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
               //.requestMatchers("/admin/**").hasRole("ADMIN")
              //  .requestMatchers("/vente/**").hasRole("VENDEUR")
              //  .requestMatchers("/profil/**").hasAnyRole("UTILISATEUR", "VENDEUR", "ADMIN")
              //  .requestMatchers("/encheres", "/login", "/logout","/vente/**","/profil/**","/inscription/**").permitAll()
             //   .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
            	.requestMatchers("/**").permitAll()
                
            )
            .formLogin(form -> form
                .loginPage("/login")
                .usernameParameter("pseudo")  
                .passwordParameter("password")
                .defaultSuccessUrl("/encheres", false) // pour envoyer vers la page demandée precedement
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
            )
            .rememberMe(remember -> remember
                .tokenValiditySeconds(7 * 24 * 60 * 60)
                .key("maCleSecretePourRememberMe")
                .userDetailsService(userDetailsManager)
            );

        return http.build();
    }

    @Bean
    public UserDetailsManager users(DataSource dataSource) {
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);

        users.setUsersByUsernameQuery(
            "SELECT pseudo AS username, motDePasse AS password, 1 AS enabled FROM Utilisateur WHERE pseudo = ?"
        );

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
