package fr.eni.encheres.configuration;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserDetailsManager userDetailsManager) throws Exception {
        http
           
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/login", "/inscription/**", "/encheres","/utilisateur/**").permitAll()
                .requestMatchers("/vente/**", "/profil/**").authenticated()
                .anyRequest().authenticated()
            )
           
            .formLogin(form -> form
                .loginPage("/login")          
                .usernameParameter("pseudo")  
                .passwordParameter("password")
                .defaultSuccessUrl("/encheres", false) 
                .permitAll()                  
            )
            
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .deleteCookies("JSESSIONID", "remember-me") 
                .permitAll()
            )
         
            .rememberMe(remember -> remember
                .tokenValiditySeconds(7 * 24 * 60 * 60)  
                .key("maCleSecretePourRememberMe")       
                .userDetailsService(userDetailsManager)
            )
           
            ;

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
