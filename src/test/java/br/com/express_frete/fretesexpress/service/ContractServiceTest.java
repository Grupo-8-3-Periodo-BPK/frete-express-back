package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.dto.ContractDTO;
import br.com.express_frete.fretesexpress.model.*;
import br.com.express_frete.fretesexpress.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
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
        when(contractRepository.findAll()).thenReturn(List.of(new Contract(), new Contract()));

        List<Contract> result = contractService.findAll();
        assertEquals(2, result.size());
        verify(contractRepository).findAll();
    }

    @Test
    void testFindByIdFound() {
        Contract contract = new Contract();
        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));

        Contract result = contractService.findById(1L);
        assertEquals(contract, result);
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
        when(contractRepository.save(any(Contract.class))).thenAnswer(i -> i.getArgument(0));

        Contract result = contractService.create(dto);
        assertNotNull(result);
        verify(contractRepository).save(any(Contract.class));
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
    void testSetDriverAcceptance() {
        Contract contract = new Contract();
        contract.setDriverAccepted(false);
        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        when(contractRepository.save(any())).thenReturn(contract);

        Contract result = contractService.setDriverAcceptance(1L, true);
        assertTrue(result.getDriverAccepted());
    }

    @Test
    void testSetClientAcceptance() {
        Contract contract = new Contract();
        contract.setClientAccepted(false);
        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        when(contractRepository.save(any())).thenReturn(contract);

        Contract result = contractService.setClientAcceptance(1L, true);
        assertTrue(result.getClientAccepted());
    }

    @Test
    void testDelete() {
        Contract contract = new Contract();
        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        when(trackingRepository.findByContract(contract)).thenReturn(List.of(new Tracking(), new Tracking()));

        contractService.delete(1L);
        verify(trackingRepository).deleteAll(anyList());
        verify(contractRepository).delete(contract);
    }

    @Test
    void testFindByClient() {
        User client = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(client));
        when(contractRepository.findByClient(client)).thenReturn(List.of(new Contract()));

        List<Contract> result = contractService.findByClient(1L);
        assertEquals(1, result.size());
    }

    @Test
    void testFindByDriver() {
        User driver = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(driver));
        when(contractRepository.findByDriver(driver)).thenReturn(List.of(new Contract()));

        List<Contract> result = contractService.findByDriver(1L);
        assertEquals(1, result.size());
    }

    @Test
    void testFindByFreight() {
        Freight freight = new Freight();
        when(freightRepository.findById(1L)).thenReturn(Optional.of(freight));
        when(contractRepository.findByFreight(freight)).thenReturn(List.of(new Contract()));

        List<Contract> result = contractService.findByFreight(1L);
        assertEquals(1, result.size());
    }

    // Utilitários para mocks
    private ContractDTO createMockDTO() {
        ContractDTO dto = new ContractDTO();
        dto.setClient_id(1L);
        dto.setDriver_id(2L);
        dto.setFreight_id(3L);
        dto.setVehicle_id(4L);
        dto.setPickup_date(LocalDate.now());
        dto.setDelivery_date(LocalDate.now().plusDays(1));
        dto.setAgreed_value(10.0);
        dto.setClient_accepted(true);
        dto.setDriver_accepted(false);
        return dto;
    }

    private void mockEntityFetches(ContractDTO dto) {
        when(userRepository.findById(dto.getClient_id())).thenReturn(Optional.of(new User()));
        when(userRepository.findById(dto.getDriver_id())).thenReturn(Optional.of(new User()));
        when(freightRepository.findById(dto.getFreight_id())).thenReturn(Optional.of(new Freight()));
        when(vehicleRepository.findById(dto.getVehicle_id())).thenReturn(Optional.of(new Vehicle()));
    }
}
