package br.com.express_frete.fretesexpress;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("br.com.express_frete.fretesexpress.repository")
public class FretesexpressApplication {

	public static void main(String[] args) {
		SpringApplication.run(FretesexpressApplication.class, args);
	}

}


