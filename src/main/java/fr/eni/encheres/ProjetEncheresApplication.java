package fr.eni.encheres;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProjetEncheresApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjetEncheresApplication.class, args);
	}

}
