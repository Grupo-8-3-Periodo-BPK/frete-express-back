package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.dto.ContractDTO;
import br.com.express_frete.fretesexpress.dto.ContractResponseDTO;
import br.com.express_frete.fretesexpress.model.*;
import br.com.express_frete.fretesexpress.model.enums.Status;
import br.com.express_frete.fretesexpress.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ContractServiceTest {

    @InjectMocks
    private ContractService contractService;

    @Mock private ContractRepository contractRepository;
    @Mock private UserRepository userRepository;
    @Mock private FreightRepository freightRepository;
    @Mock private VehicleRepository vehicleRepository;
    @Mock private TrackingRepository trackingRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        Contract contract = createCompleteMockContract();
        when(contractRepository.findAll()).thenReturn(List.of(contract));

        List<ContractResponseDTO> result = contractService.findAll();
        assertEquals(1, result.size());
        assertEquals("Motorista", result.get(0).getDriverName());
    }

    @Test
    void testFindByIdFound() {
        Contract contract = createCompleteMockContract();
        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));

        ContractResponseDTO result = contractService.findById(1L);
        assertNotNull(result);
        assertEquals("Motorista", result.getDriverName());
        assertEquals("Cliente", result.getClientName());
    }

    @Test
    void testFindByIdNotFound() {
        when(contractRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> contractService.findById(1L));
    }

    @Test
    void testCreateContract() {
        ContractDTO dto = createMockDTO();
        mockEntityFetches(dto);

        when(contractRepository.save(any(Contract.class))).thenAnswer(i -> {
            Contract saved = i.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Contract result = contractService.create(dto);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testUpdateContract() {
        ContractDTO dto = createMockDTO();
        Contract existing = new Contract();
        when(contractRepository.findById(1L)).thenReturn(Optional.of(existing));
        mockEntityFetches(dto);
        when(contractRepository.save(any(Contract.class))).thenReturn(existing);

        Contract result = contractService.update(1L, dto);
        assertNotNull(result);
    }

    @Test
    void testDelete() {
        Contract contract = createCompleteMockContract();
        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        when(trackingRepository.findByContract(contract)).thenReturn(List.of(new Tracking(), new Tracking()));

        contractService.delete(1L);
        verify(trackingRepository).deleteAll(anyList());
        verify(contractRepository).delete(contract);
    }

    @Test
    void testFindByClient() {
        User client = new User();
        client.setId(1L);
        client.setName("Cliente");

        Contract contract = createCompleteMockContract();
        contract.setClient(client);

        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(contractRepository.findByClient(client)).thenReturn(List.of(contract));

        List<ContractResponseDTO> result = contractService.findByClient(1L);
        assertEquals(1, result.size());
        assertEquals("Cliente", result.get(0).getClientName());
    }

    @Test
    void testFindByDriver() {
        User driver = new User();
        driver.setId(1L);
        driver.setName("Motorista");

        Contract contract = createCompleteMockContract();
        contract.setDriver(driver);

        when(userRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(contractRepository.findByDriver(driver)).thenReturn(List.of(contract));

        List<ContractResponseDTO> result = contractService.findByDriver(1L);
        assertEquals(1, result.size());
        assertEquals("Motorista", result.get(0).getDriverName());
    }

    @Test
    void testFindByFreight() {
        Freight freight = new Freight();
        freight.setId(1L);
        freight.setOrigin_city("Origem");
        freight.setDestination_city("Destino");

        Contract contract = createCompleteMockContract();
        contract.setFreight(freight);

        when(freightRepository.findById(1L)).thenReturn(Optional.of(freight));
        when(contractRepository.findByFreight(freight)).thenReturn(List.of(contract));

        List<ContractResponseDTO> result = contractService.findByFreight(1L);
        assertEquals(1, result.size());
    }

    // ====== Utilitários de Mock ======
    private ContractDTO createMockDTO() {
        ContractDTO dto = new ContractDTO();
        dto.setClient_id(1L);
        dto.setDriver_id(2L);
        dto.setFreight_id(3L);
        dto.setVehicle_id(4L);
        dto.setPickup_date(LocalDate.now());
        dto.setDelivery_date(LocalDate.now().plusDays(1));
        dto.setAgreed_value(100.0);
        return dto;
    }

    private void mockEntityFetches(ContractDTO dto) {
        User client = new User();
        client.setId(dto.getClient_id());
        User driver = new User();
        driver.setId(dto.getDriver_id());
        Freight freight = new Freight();
        freight.setId(dto.getFreight_id());
        Vehicle vehicle = new Vehicle();
        vehicle.setId(dto.getVehicle_id());

        when(userRepository.findById(dto.getClient_id())).thenReturn(Optional.of(client));
        when(userRepository.findById(dto.getDriver_id())).thenReturn(Optional.of(driver));
        when(freightRepository.findById(dto.getFreight_id())).thenReturn(Optional.of(freight));
        when(vehicleRepository.findById(dto.getVehicle_id())).thenReturn(Optional.of(vehicle));
    }

    private Contract createCompleteMockContract() {
        Contract contract = new Contract();
        contract.setId(1L);
        contract.setPickupDate(LocalDate.now());
        contract.setDeliveryDate(LocalDate.now().plusDays(1));
        contract.setAgreedValue(100.0);
        contract.setStatus(Status.PENDING_CLIENT_APPROVAL);

        User driver = new User();
        driver.setId(2L);
        driver.setName("Motorista");

        User client = new User();
        client.setId(3L);
        client.setName("Cliente");

        Freight freight = new Freight();
        freight.setId(4L);
        freight.setOrigin_city("Origem");
        freight.setDestination_city("Destino");

        Vehicle vehicle = new Vehicle();
        vehicle.setId(5L);

        contract.setDriver(driver);
        contract.setClient(client);
        contract.setFreight(freight);
        contract.setVehicle(vehicle);

        return contract;
    }
}
