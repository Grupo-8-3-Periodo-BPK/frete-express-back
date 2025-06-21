package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.dto.ContractDTO;
import br.com.express_frete.fretesexpress.dto.ContractResponseDTO;
import br.com.express_frete.fretesexpress.model.Contract;
import br.com.express_frete.fretesexpress.model.enums.Status;
import br.com.express_frete.fretesexpress.service.ContractService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ContractControllerTest {

    @Mock
    private ContractService contractService;

    @InjectMocks
    private ContractController contractController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private ContractDTO contractDTO;
    private ContractResponseDTO contractResponseDTO;
    private Contract contract;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(contractController).build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Para suporte a LocalDate

        // Setup de dados de teste
        contractDTO = new ContractDTO();
        contractDTO.setId(1L);
        contractDTO.setClient_id(1L);
        contractDTO.setDriver_id(2L);
        contractDTO.setFreight_id(1L);
        contractDTO.setVehicle_id(1L);
        contractDTO.setPickup_date(LocalDate.now());
        contractDTO.setDelivery_date(LocalDate.now().plusDays(7));
        contractDTO.setAgreed_value(2500.00);
        contractDTO.setDisplay_name("Contrato Teste");

        contractResponseDTO = new ContractResponseDTO(
                1L,
                "Frete: São Paulo → Rio de Janeiro (21/06/2025)",
                2500.00,
                Status.ACTIVE,
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                "Motorista Teste",
                "Cliente Teste",
                1L,
                1L,
                2L,
                1L
        );

        contract = new Contract();
        contract.setId(1L);
        contract.setAgreedValue(2500.00);
        contract.setStatus(Status.ACTIVE);
        contract.setPickupDate(LocalDate.now());
        contract.setDeliveryDate(LocalDate.now().plusDays(7));
    }

    @Test
    void testGetAllContracts() throws Exception {

        List<ContractResponseDTO> contracts = Arrays.asList(contractResponseDTO);
        when(contractService.findAll()).thenReturn(contracts);


        mockMvc.perform(get("/api/contracts"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].displayName").value("Frete: São Paulo → Rio de Janeiro (21/06/2025)"))
                .andExpect(jsonPath("$[0].agreedValue").value(2500.00))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$[0].driverName").value("Motorista Teste"))
                .andExpect(jsonPath("$[0].clientName").value("Cliente Teste"));

        verify(contractService, times(1)).findAll();
    }

    @Test
    void testGetContractById() throws Exception {

        when(contractService.findById(1L)).thenReturn(contractResponseDTO);


        mockMvc.perform(get("/api/contracts/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.displayName").value("Frete: São Paulo → Rio de Janeiro (21/06/2025)"))
                .andExpect(jsonPath("$.agreedValue").value(2500.00))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(contractService, times(1)).findById(1L);
    }

    @Test
    void testCreateContract() throws Exception {

        when(contractService.create(any(ContractDTO.class))).thenReturn(contract);


        mockMvc.perform(post("/api/contracts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contractDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.agreedValue").value(2500.00))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(contractService, times(1)).create(any(ContractDTO.class));
    }

    @Test
    void testCreateContractWithInvalidData() throws Exception {

        ContractDTO invalidContractDTO = new ContractDTO();


        mockMvc.perform(post("/api/contracts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidContractDTO)))
                .andExpect(status().isBadRequest());

        verify(contractService, never()).create(any(ContractDTO.class));
    }

    @Test
    void testUpdateContract() throws Exception {

        when(contractService.update(eq(1L), any(ContractDTO.class))).thenReturn(contract);


        mockMvc.perform(put("/api/contracts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(contractDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.agreedValue").value(2500.00));

        verify(contractService, times(1)).update(eq(1L), any(ContractDTO.class));
    }

    @Test
    void testDeleteContract() throws Exception {

        doNothing().when(contractService).delete(1L);


        mockMvc.perform(delete("/api/contracts/1"))
                .andExpect(status().isNoContent());

        verify(contractService, times(1)).delete(1L);
    }

    @Test
    void testGetContractsByClient() throws Exception {

        List<ContractResponseDTO> contracts = Arrays.asList(contractResponseDTO);
        when(contractService.findByClient(1L)).thenReturn(contracts);


        mockMvc.perform(get("/api/contracts/client/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].clientName").value("Cliente Teste"));

        verify(contractService, times(1)).findByClient(1L);
    }

    @Test
    void testGetContractsByDriver() throws Exception {

        List<ContractResponseDTO> contracts = Arrays.asList(contractResponseDTO);
        when(contractService.findByDriver(2L)).thenReturn(contracts);


        mockMvc.perform(get("/api/contracts/driver/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].driverName").value("Motorista Teste"));

        verify(contractService, times(1)).findByDriver(2L);
    }

    @Test
    void testGetContractsByFreight() throws Exception {

        List<ContractResponseDTO> contracts = Arrays.asList(contractResponseDTO);
        when(contractService.findByFreight(1L)).thenReturn(contracts);


        mockMvc.perform(get("/api/contracts/freight/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].freightId").value(1));

        verify(contractService, times(1)).findByFreight(1L);
    }

    @Test
    void testApproveContract() throws Exception {

        ContractResponseDTO approvedContract = new ContractResponseDTO(
                1L,
                "Frete: São Paulo → Rio de Janeiro (21/06/2025)",
                2500.00,
                Status.ACTIVE,
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                "Motorista Teste",
                "Cliente Teste",
                1L,
                1L,
                2L,
                1L
        );
        when(contractService.approveContract(1L)).thenReturn(approvedContract);


        mockMvc.perform(patch("/api/contracts/1/approve"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(contractService, times(1)).approveContract(1L);
    }

    @Test
    void testCancelByDriver() throws Exception {

        ContractResponseDTO cancelledContract = new ContractResponseDTO(
                1L,
                "Frete: São Paulo → Rio de Janeiro (21/06/2025)",
                2500.00,
                Status.CANCELLED_BY_DRIVER,
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                "Motorista Teste",
                "Cliente Teste",
                1L,
                1L,
                2L,
                1L
        );
        when(contractService.updateStatus(1L, Status.CANCELLED_BY_DRIVER)).thenReturn(cancelledContract);


        mockMvc.perform(patch("/api/contracts/1/cancel-by-driver"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CANCELLED_BY_DRIVER"));

        verify(contractService, times(1)).updateStatus(1L, Status.CANCELLED_BY_DRIVER);
    }

    @Test
    void testCancelByClient() throws Exception {

        ContractResponseDTO cancelledContract = new ContractResponseDTO(
                1L,
                "Frete: São Paulo → Rio de Janeiro (21/06/2025)",
                2500.00,
                Status.CANCELLED_BY_CLIENT,
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                "Motorista Teste",
                "Cliente Teste",
                1L,
                1L,
                2L,
                1L
        );
        when(contractService.updateStatus(1L, Status.CANCELLED_BY_CLIENT)).thenReturn(cancelledContract);


        mockMvc.perform(patch("/api/contracts/1/cancel-by-client"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("CANCELLED_BY_CLIENT"));

        verify(contractService, times(1)).updateStatus(1L, Status.CANCELLED_BY_CLIENT);
    }

    @Test
    void testCompleteContract() throws Exception {

        ContractResponseDTO completedContract = new ContractResponseDTO(
                1L,
                "Frete: São Paulo → Rio de Janeiro (21/06/2025)",
                2500.00,
                Status.COMPLETED,
                LocalDate.now(),
                LocalDate.now().plusDays(7),
                "Motorista Teste",
                "Cliente Teste",
                1L,
                1L,
                2L,
                1L
        );
        when(contractService.updateStatus(1L, Status.COMPLETED)).thenReturn(completedContract);


        mockMvc.perform(patch("/api/contracts/1/complete"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        verify(contractService, times(1)).updateStatus(1L, Status.COMPLETED);
    }

    @Test
    void testGetContractsByClientEmpty() throws Exception {

        when(contractService.findByClient(999L)).thenReturn(Arrays.asList());


        mockMvc.perform(get("/api/contracts/client/999"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(contractService, times(1)).findByClient(999L);
    }

    @Test
    void testGetContractsByDriverEmpty() throws Exception {

        when(contractService.findByDriver(999L)).thenReturn(Arrays.asList());


        mockMvc.perform(get("/api/contracts/driver/999"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(contractService, times(1)).findByDriver(999L);
    }

    @Test
    void testGetContractsByFreightEmpty() throws Exception {

        when(contractService.findByFreight(999L)).thenReturn(Arrays.asList());


        mockMvc.perform(get("/api/contracts/freight/999"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());

        verify(contractService, times(1)).findByFreight(999L);
    }
}