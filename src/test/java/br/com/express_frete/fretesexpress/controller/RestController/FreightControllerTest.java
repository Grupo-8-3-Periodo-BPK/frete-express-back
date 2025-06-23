package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.dto.FreightRequestDTO;
import br.com.express_frete.fretesexpress.dto.FreightUpdateDTO;
import br.com.express_frete.fretesexpress.model.Contract;
import br.com.express_frete.fretesexpress.model.Freight;
import br.com.express_frete.fretesexpress.model.Tracking;
import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.model.enums.FreightStatus;
import br.com.express_frete.fretesexpress.model.enums.Role;
import br.com.express_frete.fretesexpress.model.enums.Status;
import br.com.express_frete.fretesexpress.repository.ContractRepository;
import br.com.express_frete.fretesexpress.repository.FreightRepository;
import br.com.express_frete.fretesexpress.repository.TrackingRepository;
import br.com.express_frete.fretesexpress.repository.UserRepository;
import br.com.express_frete.fretesexpress.service.FreightService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FreightControllerTest {

    @Mock
    private FreightRepository freightRepository;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private TrackingRepository trackingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FreightService freightService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private FreightController freightController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private User testUser;
    private Freight testFreight;
    private FreightRequestDTO freightRequestDTO;
    private FreightUpdateDTO freightUpdateDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(freightController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Setup test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setRole(Role.CLIENT);

        // Setup test freight
        testFreight = new Freight();
        testFreight.setId(1L);
        testFreight.setName("Test Freight");
        testFreight.setPrice(1000.0);
        testFreight.setWeight(100.0);
        testFreight.setHeight(1.0);
        testFreight.setLength(2.0);
        testFreight.setWidth(1.0);
        testFreight.setInitial_date(LocalDate.now());
        testFreight.setFinal_date(LocalDate.now().plusDays(7));
        testFreight.setOrigin_city("São Paulo");
        testFreight.setOrigin_state("SP");
        testFreight.setDestination_city("Rio de Janeiro");
        testFreight.setDestination_state("RJ");
        testFreight.setUserId(testUser.getId());
        testFreight.setStatus(FreightStatus.AVAILABLE);

        // Setup DTOs
        freightRequestDTO = new FreightRequestDTO();
        freightRequestDTO.setName("Test Freight");
        freightRequestDTO.setPrice(1000.0);
        freightRequestDTO.setWeight(100.0);
        freightRequestDTO.setHeight(1.0);
        freightRequestDTO.setLength(2.0);
        freightRequestDTO.setWidth(1.0);
        freightRequestDTO.setInitial_date(LocalDate.now());
        freightRequestDTO.setFinal_date(LocalDate.now().plusDays(7));
        freightRequestDTO.setOrigin_city("São Paulo");
        freightRequestDTO.setOrigin_state("SP");
        freightRequestDTO.setDestination_city("Rio de Janeiro");
        freightRequestDTO.setDestination_state("RJ");

        freightUpdateDTO = new FreightUpdateDTO();
        freightUpdateDTO.setName("Updated Freight");
        freightUpdateDTO.setPrice(1200.0);
        freightUpdateDTO.setWeight(120.0);
        freightUpdateDTO.setHeight(1.2);
        freightUpdateDTO.setLength(2.2);
        freightUpdateDTO.setWidth(1.2);
        freightUpdateDTO.setInitial_date(LocalDate.now().plusDays(1));
        freightUpdateDTO.setFinal_date(LocalDate.now().plusDays(8));
        freightUpdateDTO.setOrigin_city("Curitiba");
        freightUpdateDTO.setOrigin_state("PR");
        freightUpdateDTO.setDestination_city("Florianópolis");
        freightUpdateDTO.setDestination_state("SC");
    }

    private void mockAuthentication() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
    }

    private void mockNoAuthentication() {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);
    }

    @Test
    void getAllFreights_WithoutStatus_ShouldReturnAllFreights() throws Exception {
        List<Freight> freights = Arrays.asList(testFreight);
        when(freightService.findAll()).thenReturn(freights);

        mockMvc.perform(get("/api/freights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Freight"));

        verify(freightService).findAll();
        verify(freightService, never()).findByStatus(any());
    }

    @Test
    void getAllFreights_WithStatus_ShouldReturnFilteredFreights() throws Exception {
        List<Freight> freights = Arrays.asList(testFreight);
        when(freightService.findByStatus(FreightStatus.AVAILABLE)).thenReturn(freights);

        mockMvc.perform(get("/api/freights")
                .param("status", "AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Freight"));

        verify(freightService).findByStatus(FreightStatus.AVAILABLE);
        verify(freightService, never()).findAll();
    }

    @Test
    void register_WithValidData_ShouldCreateFreight() throws Exception {
        mockAuthentication();
        when(freightRepository.save(any(Freight.class))).thenReturn(testFreight);

        mockMvc.perform(post("/api/freights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(freightRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Freight"))
                .andExpect(jsonPath("$.price").value(1000.0));

        verify(freightRepository).save(any(Freight.class));
    }

    @Test
    void register_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockNoAuthentication();

        mockMvc.perform(post("/api/freights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(freightRequestDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Usuário não autenticado")));

        verify(freightRepository, never()).save(any());
    }

    @Test
    void register_WithInvalidPrincipal_ShouldReturnUnauthorized() throws Exception {
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("invalid_principal");

        mockMvc.perform(post("/api/freights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(freightRequestDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("tipo de principal inválido")));

        verify(freightRepository, never()).save(any());
    }

    @Test
    void getById_WithExistingFreight_ShouldReturnFreight() throws Exception {
        when(freightRepository.findById(1L)).thenReturn(Optional.of(testFreight));

        mockMvc.perform(get("/api/freights/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Freight"));

        verify(freightRepository).findById(1L);
    }

    @Test
    void getById_WithNonExistingFreight_ShouldReturnNotFound() throws Exception {
        when(freightRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/freights/999"))
                .andExpect(status().isNotFound());

        verify(freightRepository).findById(999L);
    }

    @Test
    void update_WithValidDataAndOwner_ShouldUpdateFreight() throws Exception {
        mockAuthentication();
        when(freightRepository.findById(1L)).thenReturn(Optional.of(testFreight));
        when(freightRepository.save(any(Freight.class))).thenReturn(testFreight);

        mockMvc.perform(put("/api/freights/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(freightUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").exists());

        verify(freightRepository).findById(1L);
        verify(freightRepository).save(any(Freight.class));
    }

    @Test
    void update_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockNoAuthentication();

        mockMvc.perform(put("/api/freights/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(freightUpdateDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Usuário não autenticado")));

        verify(freightRepository, never()).findById(anyLong());
        verify(freightRepository, never()).save(any());
    }

    @Test
    void update_WithDifferentOwner_ShouldReturnForbidden() throws Exception {
        User differentUser = new User();
        differentUser.setId(2L);
        differentUser.setName("Different User");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(differentUser);

        when(freightRepository.findById(1L)).thenReturn(Optional.of(testFreight));

        mockMvc.perform(put("/api/freights/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(freightUpdateDTO)))
                .andExpect(status().isForbidden())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("não tem permissão para editar")));

        verify(freightRepository).findById(1L);
        verify(freightRepository, never()).save(any());
    }

    @Test
    void update_WithNonExistingFreight_ShouldReturnNotFound() throws Exception {
        mockAuthentication();
        when(freightRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/freights/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(freightUpdateDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Frete não encontrado")));

        verify(freightRepository).findById(999L);
        verify(freightRepository, never()).save(any());
    }

    @Test
    void delete_WithValidOwnerAndNoActiveContracts_ShouldDeleteFreight() throws Exception {
        mockAuthentication();
        when(freightRepository.findById(1L)).thenReturn(Optional.of(testFreight));
        when(contractRepository.findByFreight(testFreight)).thenReturn(Arrays.asList());

        mockMvc.perform(delete("/api/freights/1"))
                .andExpect(status().isNoContent());

        verify(freightRepository).findById(1L);
        verify(contractRepository).findByFreight(testFreight);
        verify(freightRepository).delete(testFreight);
    }

    @Test
    void delete_WithoutAuthentication_ShouldReturnUnauthorized() throws Exception {
        mockNoAuthentication();

        mockMvc.perform(delete("/api/freights/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Usuário não autenticado")));

        verify(freightRepository, never()).findById(anyLong());
        verify(freightRepository, never()).delete(any());
    }

    @Test
    void delete_WithDifferentOwner_ShouldReturnForbidden() throws Exception {
        User differentUser = new User();
        differentUser.setId(2L);
        differentUser.setName("Different User");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(differentUser);

        when(freightRepository.findById(1L)).thenReturn(Optional.of(testFreight));

        mockMvc.perform(delete("/api/freights/1"))
                .andExpect(status().isForbidden())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("não tem permissão para excluir")));

        verify(freightRepository).findById(1L);
        verify(freightRepository, never()).delete(any());
    }

    @Test
    void delete_WithActiveContracts_ShouldReturnConflict() throws Exception {
        mockAuthentication();

        Contract activeContract = new Contract();
        activeContract.setId(1L);
        activeContract.setStatus(Status.ACTIVE);

        when(freightRepository.findById(1L)).thenReturn(Optional.of(testFreight));
        when(contractRepository.findByFreight(testFreight)).thenReturn(Arrays.asList(activeContract));

        mockMvc.perform(delete("/api/freights/1"))
                .andExpect(status().isConflict())
                .andExpect(content()
                        .string(org.hamcrest.Matchers.containsString("contratos ativos, em andamento ou concluídos")));

        verify(freightRepository).findById(1L);
        verify(contractRepository).findByFreight(testFreight);
        verify(freightRepository, never()).delete(any());
    }

    @Test
    void delete_WithCompletedContracts_ShouldReturnConflict() throws Exception {
        mockAuthentication();

        Contract completedContract = new Contract();
        completedContract.setId(1L);
        completedContract.setStatus(Status.COMPLETED);

        when(freightRepository.findById(1L)).thenReturn(Optional.of(testFreight));
        when(contractRepository.findByFreight(testFreight)).thenReturn(Arrays.asList(completedContract));

        mockMvc.perform(delete("/api/freights/1"))
                .andExpect(status().isConflict())
                .andExpect(content()
                        .string(org.hamcrest.Matchers.containsString("contratos ativos, em andamento ou concluídos")));

        verify(freightRepository).findById(1L);
        verify(contractRepository).findByFreight(testFreight);
        verify(freightRepository, never()).delete(any());
    }

    @Test
    void delete_WithPendingContracts_ShouldDeleteFreightAndContracts() throws Exception {
        mockAuthentication();

        Contract pendingContract = new Contract();
        pendingContract.setId(1L);
        pendingContract.setStatus(Status.PENDING_CLIENT_APPROVAL);

        Tracking tracking = new Tracking();
        tracking.setId(1L);

        when(freightRepository.findById(1L)).thenReturn(Optional.of(testFreight));
        when(contractRepository.findByFreight(testFreight)).thenReturn(Arrays.asList(pendingContract));
        when(trackingRepository.findByContract(pendingContract)).thenReturn(Optional.of(tracking));

        mockMvc.perform(delete("/api/freights/1"))
                .andExpect(status().isNoContent());

        verify(freightRepository).findById(1L);
        verify(contractRepository).findByFreight(testFreight);
        verify(trackingRepository).findByContract(pendingContract);
        verify(trackingRepository).delete(tracking);
        verify(contractRepository).deleteAll(Arrays.asList(pendingContract));
        verify(freightRepository).delete(testFreight);
    }

    @Test
    void delete_WithNonExistingFreight_ShouldReturnNotFound() throws Exception {
        mockAuthentication();
        when(freightRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/freights/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Frete não encontrado")));

        verify(freightRepository).findById(999L);
        verify(freightRepository, never()).delete(any());
    }
}