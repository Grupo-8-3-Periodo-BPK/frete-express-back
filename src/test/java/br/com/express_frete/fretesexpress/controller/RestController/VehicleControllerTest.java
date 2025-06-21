package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.model.Vehicle;
import br.com.express_frete.fretesexpress.model.enums.BodyType;
import br.com.express_frete.fretesexpress.model.enums.Role;
import br.com.express_frete.fretesexpress.model.enums.VehicleCategory;
import br.com.express_frete.fretesexpress.service.VehicleService;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class VehicleControllerTest {

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private VehicleController vehicleController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Vehicle testVehicle;
    private User testUser;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(vehicleController).build();
        objectMapper = new ObjectMapper();

        // Criar usuário de teste
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Motorista Teste");
        testUser.setEmail("motorista@test.com");
        testUser.setRole(Role.DRIVER);
        testUser.setCpfCnpj("12345678901");
        testUser.setPhone("(11) 99999-9999");
        testUser.setCnh("12345678900");

        // Criar veículo de teste
        testVehicle = new Vehicle();
        testVehicle.setId(1L);
        testVehicle.setLicensePlate("ABC1D23");
        testVehicle.setRenavam("12345678901");
        testVehicle.setBrand("Volvo");
        testVehicle.setModel("Caminhão Baú");
        testVehicle.setYear(2020);
        testVehicle.setColor("Branco");
        testVehicle.setWeight(4000.0);
        testVehicle.setLength(12.0);
        testVehicle.setWidth(2.5);
        testVehicle.setHeight(3.0);
        testVehicle.setAxlesCount(3);
        testVehicle.setHasCanvas(false);
        testVehicle.setCategory(VehicleCategory.TRUCK);
        testVehicle.setBodyType(BodyType.BOX);
        testVehicle.setUser(testUser);
    }

    @Test
    void findAll_WhenVehiclesExist_ShouldReturnOkWithVehiclesList() throws Exception {

        when(vehicleService.findAll()).thenReturn(Arrays.asList(testVehicle));


        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].licensePlate").value("ABC1D23"))
                .andExpect(jsonPath("$[0].brand").value("Volvo"));

        verify(vehicleService).findAll();
    }

    @Test
    void findAll_WhenNoVehiclesExist_ShouldReturnNotFound() throws Exception {

        when(vehicleService.findAll()).thenReturn(Collections.emptyList());


        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isNotFound());

        verify(vehicleService).findAll();
    }

    @Test
    void findByUserId_WhenVehiclesExist_ShouldReturnOkWithVehiclesList() throws Exception {

        Long userId = 1L;
        when(vehicleService.findByUserId(userId)).thenReturn(Arrays.asList(testVehicle));


        mockMvc.perform(get("/api/vehicles/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].licensePlate").value("ABC1D23"));

        verify(vehicleService).findByUserId(userId);
    }

    @Test
    void findByUserId_WhenNoVehiclesExist_ShouldReturnNotFound() throws Exception {

        Long userId = 1L;
        when(vehicleService.findByUserId(userId)).thenReturn(Collections.emptyList());


        mockMvc.perform(get("/api/vehicles/user/{userId}", userId))
                .andExpect(status().isNotFound());

        verify(vehicleService).findByUserId(userId);
    }

    @Test
    void create_WhenValidVehicle_ShouldReturnCreatedWithVehicle() throws Exception {

        when(vehicleService.existsByLicensePlate(anyString())).thenReturn(false);
        when(vehicleService.existsByRenavam(anyString())).thenReturn(false);
        when(vehicleService.save(any(Vehicle.class))).thenReturn(testVehicle);


        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testVehicle)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.licensePlate").value("ABC1D23"));

        verify(vehicleService).existsByLicensePlate("ABC1D23");
        verify(vehicleService).existsByRenavam("12345678901");
        verify(vehicleService).save(any(Vehicle.class));
    }

    @Test
    void create_WhenLicensePlateExists_ShouldReturnBadRequest() throws Exception {

        when(vehicleService.existsByLicensePlate(anyString())).thenReturn(true);


        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testVehicle)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Placa ja cadastrada no sistema"));

        verify(vehicleService).existsByLicensePlate("ABC1D23");
        verify(vehicleService, never()).save(any(Vehicle.class));
    }

    @Test
    void create_WhenRenavamExists_ShouldReturnBadRequest() throws Exception {

        when(vehicleService.existsByLicensePlate(anyString())).thenReturn(false);
        when(vehicleService.existsByRenavam(anyString())).thenReturn(true);


        mockMvc.perform(post("/api/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testVehicle)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Renavam ja cadastrado no sistema"));

        verify(vehicleService).existsByLicensePlate("ABC1D23");
        verify(vehicleService).existsByRenavam("12345678901");
        verify(vehicleService, never()).save(any(Vehicle.class));
    }

    @Test
    void findById_WhenVehicleExists_ShouldReturnOkWithVehicle() throws Exception {

        Long vehicleId = 1L;
        when(vehicleService.findById(vehicleId)).thenReturn(Optional.of(testVehicle));


        mockMvc.perform(get("/api/vehicles/{id}", vehicleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.licensePlate").value("ABC1D23"));

        verify(vehicleService).findById(vehicleId);
    }

    @Test
    void findById_WhenVehicleNotExists_ShouldReturnNotFound() throws Exception {

        Long vehicleId = 1L;
        when(vehicleService.findById(vehicleId)).thenReturn(Optional.empty());


        mockMvc.perform(get("/api/vehicles/{id}", vehicleId))
                .andExpect(status().isNotFound());

        verify(vehicleService).findById(vehicleId);
    }

    @Test
    void findByLicensePlate_WhenVehicleExists_ShouldReturnOkWithVehicle() throws Exception {

        String licensePlate = "ABC1D23";
        when(vehicleService.findByLicensePlate(licensePlate)).thenReturn(Optional.of(testVehicle));


        mockMvc.perform(get("/api/vehicles/plate/{plate}", licensePlate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.licensePlate").value("ABC1D23"));

        verify(vehicleService).findByLicensePlate(licensePlate);
    }

    @Test
    void findByLicensePlate_WhenVehicleNotExists_ShouldReturnNotFound() throws Exception {

        String licensePlate = "ABC1D23";
        when(vehicleService.findByLicensePlate(licensePlate)).thenReturn(Optional.empty());


        mockMvc.perform(get("/api/vehicles/plate/{plate}", licensePlate))
                .andExpect(status().isNotFound());

        verify(vehicleService).findByLicensePlate(licensePlate);
    }

    @Test
    void update_WhenValidUpdate_ShouldReturnOkWithUpdatedVehicle() throws Exception {

        Long vehicleId = 1L;
        Vehicle updatedVehicle = new Vehicle();
        updatedVehicle.setId(vehicleId);
        updatedVehicle.setLicensePlate("XYZ9Z88");
        updatedVehicle.setRenavam("98765432101");
        updatedVehicle.setBrand("Scania");
        updatedVehicle.setModel("Carreta");
        updatedVehicle.setYear(2021);
        updatedVehicle.setColor("Azul");
        updatedVehicle.setWeight(5000.0);
        updatedVehicle.setLength(15.0);
        updatedVehicle.setWidth(2.8);
        updatedVehicle.setHeight(3.5);
        updatedVehicle.setAxlesCount(4);
        updatedVehicle.setHasCanvas(true);
        updatedVehicle.setCategory(VehicleCategory.TRAILER);
        updatedVehicle.setBodyType(BodyType.BOX);
        updatedVehicle.setUser(testUser);

        when(vehicleService.findById(vehicleId)).thenReturn(Optional.of(testVehicle));
        when(vehicleService.findByLicensePlate("XYZ9Z88")).thenReturn(Optional.empty());
        when(vehicleService.findByRenavam("98765432101")).thenReturn(Optional.empty());
        when(vehicleService.save(any(Vehicle.class))).thenReturn(updatedVehicle);


        mockMvc.perform(put("/api/vehicles/{id}", vehicleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedVehicle)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(vehicleId))
                .andExpect(jsonPath("$.licensePlate").value("XYZ9Z88"))
                .andExpect(jsonPath("$.brand").value("Scania"));

        verify(vehicleService).findById(vehicleId);
        verify(vehicleService).findByLicensePlate("XYZ9Z88");
        verify(vehicleService).findByRenavam("98765432101");
        verify(vehicleService).save(any(Vehicle.class));
    }

    @Test
    void update_WhenVehicleNotExists_ShouldReturnNotFound() throws Exception {

        Long vehicleId = 1L;
        when(vehicleService.findById(vehicleId)).thenReturn(Optional.empty());


        mockMvc.perform(put("/api/vehicles/{id}", vehicleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testVehicle)))
                .andExpect(status().isNotFound());

        verify(vehicleService).findById(vehicleId);
        verify(vehicleService, never()).save(any(Vehicle.class));
    }

    @Test
    void update_WhenLicensePlateExistsForAnotherVehicle_ShouldReturnBadRequest() throws Exception {

        Long vehicleId = 1L;
        Vehicle anotherVehicle = new Vehicle();
        anotherVehicle.setId(2L);
        anotherVehicle.setLicensePlate("ABC1D23");

        when(vehicleService.findById(vehicleId)).thenReturn(Optional.of(testVehicle));
        when(vehicleService.findByLicensePlate("ABC1D23")).thenReturn(Optional.of(anotherVehicle));


        mockMvc.perform(put("/api/vehicles/{id}", vehicleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testVehicle)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Placa ja cadastrada para outro veiculo"));

        verify(vehicleService).findById(vehicleId);
        verify(vehicleService).findByLicensePlate("ABC1D23");
        verify(vehicleService, never()).save(any(Vehicle.class));
    }

    @Test
    void update_WhenRenavamExistsForAnotherVehicle_ShouldReturnBadRequest() throws Exception {

        Long vehicleId = 1L;
        Vehicle anotherVehicle = new Vehicle();
        anotherVehicle.setId(2L);
        anotherVehicle.setRenavam("12345678901");

        when(vehicleService.findById(vehicleId)).thenReturn(Optional.of(testVehicle));
        when(vehicleService.findByLicensePlate("ABC1D23")).thenReturn(Optional.of(testVehicle));
        when(vehicleService.findByRenavam("12345678901")).thenReturn(Optional.of(anotherVehicle));


        mockMvc.perform(put("/api/vehicles/{id}", vehicleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testVehicle)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Renavam ja cadastrado para outro veiculo"));

        verify(vehicleService).findById(vehicleId);
        verify(vehicleService).findByLicensePlate("ABC1D23");
        verify(vehicleService).findByRenavam("12345678901");
        verify(vehicleService, never()).save(any(Vehicle.class));
    }

    @Test
    void deleteById_WhenVehicleExists_ShouldReturnNoContent() throws Exception {

        Long vehicleId = 1L;
        when(vehicleService.findById(vehicleId)).thenReturn(Optional.of(testVehicle));


        mockMvc.perform(delete("/api/vehicles/{id}", vehicleId))
                .andExpect(status().isNoContent());

        verify(vehicleService).findById(vehicleId);
        verify(vehicleService).deleteById(vehicleId);
    }

    @Test
    void deleteById_WhenVehicleNotExists_ShouldReturnNotFound() throws Exception {

        Long vehicleId = 1L;
        when(vehicleService.findById(vehicleId)).thenReturn(Optional.empty());


        mockMvc.perform(delete("/api/vehicles/{id}", vehicleId))
                .andExpect(status().isNotFound());

        verify(vehicleService).findById(vehicleId);
        verify(vehicleService, never()).deleteById(vehicleId);
    }

    @Test
    void update_WhenSameLicensePlateForSameVehicle_ShouldReturnOk() throws Exception {

        Long vehicleId = 1L;
        when(vehicleService.findById(vehicleId)).thenReturn(Optional.of(testVehicle));
        when(vehicleService.findByLicensePlate("ABC1D23")).thenReturn(Optional.of(testVehicle));
        when(vehicleService.findByRenavam("12345678901")).thenReturn(Optional.of(testVehicle));
        when(vehicleService.save(any(Vehicle.class))).thenReturn(testVehicle);


        mockMvc.perform(put("/api/vehicles/{id}", vehicleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testVehicle)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(vehicleId));

        verify(vehicleService).findById(vehicleId);
        verify(vehicleService).findByLicensePlate("ABC1D23");
        verify(vehicleService).findByRenavam("12345678901");
        verify(vehicleService).save(any(Vehicle.class));
    }
}