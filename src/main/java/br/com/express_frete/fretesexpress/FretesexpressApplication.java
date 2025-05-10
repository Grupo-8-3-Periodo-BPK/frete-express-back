package br.com.express_frete.fretesexpress;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.model.Token;
import br.com.express_frete.fretesexpress.model.enums.Role;
import br.com.express_frete.fretesexpress.repository.UserRepository;
import br.com.express_frete.fretesexpress.service.TokenService;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@SpringBootApplication
@EnableJpaRepositories("br.com.express_frete.fretesexpress.repository")
public class FretesexpressApplication {

	public static void main(String[] args) {
		SpringApplication.run(FretesexpressApplication.class, args);
	}

	@Bean
	public CommandLineRunner initDatabase(UserRepository userRepository,
			TokenService tokenService,
			BCryptPasswordEncoder encoder) {
		return args -> {
			if (userRepository.count() == 0) {
				System.out.println("Inicializando banco de dados com dados de teste...");

				// Criar usuário ADMIN
				User admin = new User();
				admin.setUsername("admin");
				admin.setName("Administrador");
				admin.setEmail("admin@freteexpress.com");
				admin.setPassword(encoder.encode("admin"));
				admin.setRole(Role.ADMIN);
				admin.setCpfCnpj("12345678901");
				admin.setPhone("(11) 99999-9999");
				admin = userRepository.save(admin);

				// Criar token para o admin
				tokenService.createToken(admin);

				// Criar usuário DRIVER
				User driver = new User();
				driver.setUsername("motorista");
				driver.setName("Motorista Teste");
				driver.setEmail("motorista@freteexpress.com");
				driver.setPassword(encoder.encode("motorista"));
				driver.setRole(Role.DRIVER);
				driver.setCpfCnpj("98765432101");
				driver.setPhone("(11) 88888-8888");
				driver.setCnh("12345678900");
				driver = userRepository.save(driver);

				// Criar token para o motorista
				tokenService.createToken(driver);

				// Criar usuário CLIENT
				User client = new User();
				client.setUsername("cliente");
				client.setName("Cliente Teste");
				client.setEmail("cliente@freteexpress.com");
				client.setPassword(encoder.encode("cliente"));
				client.setRole(Role.CLIENT);
				client.setCpfCnpj("45678912301");
				client.setPhone("(11) 77777-7777");
				client = userRepository.save(client);

				// Criar token para o cliente
				tokenService.createToken(client);

				System.out.println("Dados inicializados com sucesso!");
			} else {
				System.out.println("Banco de dados já possui dados. Pulando inicialização.");
			}
		};
	}
}