package fr.eni.encheres.configuration;
import javax.sql.DataSource;

/*import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;

@Configuration
public class SecurityConfig {

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .permitAll()
        )
        .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout")
        )
        // Activation de remember-me 
        .rememberMe(remember -> remember
            .tokenValiditySeconds(7 * 24 * 60 * 60)  // durée 7 jours
            .key("maCleSecretePourRememberMe")        // clé secrète (à changer)
            .userDetailsService(users(dataSource))    // injecter ton UserDetailsService
        )
        ;

    return http.build();
}
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsManager users(DataSource dataSource) {
        JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
        users.setUsersByUsernameQuery(
            "SELECT pseudo, motDePasse, true as enabled FROM UTILISATEUR WHERE pseudo = ?"
        );
        users.setAuthoritiesByUsernameQuery(
            "SELECT u.pseudo, r.role FROM ROLES r JOIN UTILISATEUR u ON r.idUtilisateur = u.idUtilisateur WHERE u.pseudo = ?"
        );
        return users;
    }

    // AuthenticationManager pour l'authentification
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}*/
