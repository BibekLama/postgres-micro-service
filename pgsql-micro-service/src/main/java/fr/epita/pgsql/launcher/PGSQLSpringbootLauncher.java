package fr.epita.pgsql.launcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages="fr.epita")
public class PGSQLSpringbootLauncher {
	
	public static void main(String[] args) {
		SpringApplication.run(PGSQLSpringbootLauncher.class, args);
	}

}
