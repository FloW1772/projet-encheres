package fr.eni.encheres.bll;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TestPassword {
    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        String[] users = {
            "alice123:passAlice",
            "bob456:passBob",
            "charlie789:passCharlie",
            "diane321:passDiane",
            "eric654:passEric"
        };

        for (String user : users) {
            String[] parts = user.split(":");
            String pseudo = parts[0];
            String rawPassword = parts[1];
            String encodedPassword = encoder.encode(rawPassword);
            System.out.println("UPDATE Utilisateur SET motDePasse = '" + encodedPassword + "' WHERE pseudo = '" + pseudo + "';");
        }
    }
}
