package br.com.express_frete.fretesexpress;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import br.com.express_frete.fretesexpress.model.Contract;
import br.com.express_frete.fretesexpress.model.Freight;
import br.com.express_frete.fretesexpress.model.Tracking;
import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.model.Vehicle;
import br.com.express_frete.fretesexpress.model.enums.Role;
import br.com.express_frete.fretesexpress.model.enums.VehicleCategory;
import br.com.express_frete.fretesexpress.model.enums.BodyType;
import br.com.express_frete.fretesexpress.repository.ContractRepository;
import br.com.express_frete.fretesexpress.repository.FreightRepository;
import br.com.express_frete.fretesexpress.repository.TrackingRepository;
import br.com.express_frete.fretesexpress.repository.UserRepository;
import br.com.express_frete.fretesexpress.repository.VehicleRepository;

import java.time.LocalDate;

@SpringBootApplication
@EnableJpaRepositories("br.com.express_frete.fretesexpress.repository")
public class FretesexpressApplication {

	public static void main(String[] args) {
		SpringApplication.run(FretesexpressApplication.class, args);
	}

	@Bean
	public CommandLineRunner initDatabase(UserRepository userRepository,
			FreightRepository freightRepository,
			ContractRepository contractRepository,
			TrackingRepository trackingRepository,
			VehicleRepository vehicleRepository,
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

				// Criar exemplo de frete
				Freight freight = new Freight();
				freight.setName("Frete de Móveis");
				freight.setUserId(client.getId());
				freight.setWeight(150.0);
				freight.setHeight(1.2);
				freight.setLength(2.5);
				freight.setWidth(1.0);
				freight.setInitial_date(LocalDate.now());
				freight.setFinal_date(LocalDate.now().plusDays(7));
				freight.setOrigin_city("São Paulo");
				freight.setOrigin_state("SP");
				freight.setDestination_city("Rio de Janeiro");
				freight.setDestination_state("RJ");
				freight = freightRepository.save(freight);

				// Criar um segundo exemplo de frete
				Freight freight2 = new Freight();
				freight2.setName("Entrega de Eletrônicos");
				freight2.setUserId(client.getId());
				freight2.setWeight(80.0);
				freight2.setHeight(0.8);
				freight2.setLength(1.5);
				freight2.setWidth(0.6);
				freight2.setInitial_date(LocalDate.now().plusDays(1));
				freight2.setFinal_date(LocalDate.now().plusDays(5));
				freight2.setOrigin_city("Curitiba");
				freight2.setOrigin_state("PR");
				freight2.setDestination_city("Florianópolis");
				freight2.setDestination_state("SC");
				freight2 = freightRepository.save(freight2);

				// Criar veículo exemplo
				Vehicle vehicle = new Vehicle();
				vehicle.setLicensePlate("ABC1D23");
				vehicle.setModel("Caminhão Baú");
				vehicle.setBrand("Volvo");
				vehicle.setYear(2020);
				vehicle.setColor("Branco");
				vehicle.setRenavam("12345678901");
				vehicle.setWeight(4000.0);
				vehicle.setLength(12.0);
				vehicle.setWidth(2.5);
				vehicle.setHeight(3.0);
				vehicle.setAxlesCount(3);
				vehicle.setHasCanvas(false);
				vehicle.setCategory(VehicleCategory.TRUCK);
				vehicle.setBodyType(BodyType.BOX);
				vehicle.setUser(driver);
				vehicle = vehicleRepository.save(vehicle);

				// Criar contrato exemplo
				Contract contract = new Contract();
				contract.setClient(client);
				contract.setDriver(driver);
				contract.setFreight(freight);
				contract.setVehicle(vehicle);
				contract.setDriverAccepted(true);
				contract.setClientAccepted(true);
				contract.setPickupDate(LocalDate.now());
				contract.setDeliveryDate(LocalDate.now().plusDays(7));
				contract.setAgreedValue(2500.00);
				contract = contractRepository.save(contract);

				// Criar outro contrato exemplo (pendente de aceitação)
				Contract contract2 = new Contract();
				contract2.setClient(client);
				contract2.setDriver(driver);
				contract2.setFreight(freight2);
				contract2.setVehicle(vehicle);
				contract2.setDriverAccepted(true);
				contract2.setClientAccepted(false);
				contract2.setPickupDate(LocalDate.now().plusDays(1));
				contract2.setDeliveryDate(LocalDate.now().plusDays(5));
				contract2.setAgreedValue(1800.00);
				contract2 = contractRepository.save(contract2);

				// Criar tracking para o primeiro contrato
				Tracking tracking1 = new Tracking();
				tracking1.setFreight(freight);
				tracking1.setContract(contract);
				tracking1.setCurrentLocation("São Paulo, SP - Depósito Central");
				tracking1.setContractUser(admin);
				trackingRepository.save(tracking1);

				// Criar outro tracking para o mesmo contrato
				Tracking tracking2 = new Tracking();
				tracking2.setFreight(freight);
				tracking2.setContract(contract);
				tracking2.setCurrentLocation("Rodovia Dutra, KM 200");
				tracking2.setContractUser(driver);
				trackingRepository.save(tracking2);

				System.out.println("Dados inicializados com sucesso!");
			} else {
				System.out.println("Banco de dados já inicializado anteriormente.");
			}
		};
	}
}