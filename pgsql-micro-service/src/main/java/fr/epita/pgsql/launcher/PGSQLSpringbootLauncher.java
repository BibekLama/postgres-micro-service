package fr.epita.pgsql.launcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@SpringBootApplication(scanBasePackages="fr.epita")
public class PGSQLSpringbootLauncher extends SpringBootServletInitializer{
	
	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(PGSQLSpringbootLauncher.class);
    }
	
	public static void main(String[] args) {
		SpringApplication.run(PGSQLSpringbootLauncher.class, args);
	}

}
