package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.model.Vehicle;
import br.com.express_frete.fretesexpress.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.lang.reflect.Field;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class VehicleServiceTest {

    private VehicleRepository vehicleRepository;
    private VehicleService vehicleService;

    @BeforeEach
    void setUp() throws Exception {
        vehicleRepository = Mockito.mock(VehicleRepository.class);
        vehicleService = new VehicleService(); // usa o construtor padrão

        // Usa Reflection para injetar o mock no campo privado
        Field repositoryField = VehicleService.class.getDeclaredField("vehicleRepository");
        repositoryField.setAccessible(true);
        repositoryField.set(vehicleService, vehicleRepository);
    }

    @Test
    void testFindAll() {
        Vehicle v1 = new Vehicle();
        Vehicle v2 = new Vehicle();
        when(vehicleRepository.findAll()).thenReturn(Arrays.asList(v1, v2));

        List<Vehicle> result = vehicleService.findAll();

        assertEquals(2, result.size());
        verify(vehicleRepository, times(1)).findAll();
    }

    @Test
    void testSave() {
        Vehicle vehicle = new Vehicle();
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        Vehicle result = vehicleService.save(vehicle);

        assertNotNull(result);
        verify(vehicleRepository, times(1)).save(vehicle);
    }

    @Test
    void testFindByUserId() {
        Long userId = 1L;
        when(vehicleRepository.findByUserId(userId)).thenReturn(List.of(new Vehicle()));

        List<Vehicle> result = vehicleService.findByUserId(userId);

        assertEquals(1, result.size());
        verify(vehicleRepository, times(1)).findByUserId(userId);
    }

    @Test
    void testFindById() {
        Long id = 1L;
        Vehicle vehicle = new Vehicle();
        when(vehicleRepository.findById(id)).thenReturn(Optional.of(vehicle));

        Optional<Vehicle> result = vehicleService.findById(id);

        assertTrue(result.isPresent());
        verify(vehicleRepository, times(1)).findById(id);
    }

    @Test
    void testFindByLicensePlate() {
        String plate = "ABC1234";
        Vehicle vehicle = new Vehicle();
        when(vehicleRepository.findByLicensePlate(plate)).thenReturn(Optional.of(vehicle));

        Optional<Vehicle> result = vehicleService.findByLicensePlate(plate);

        assertTrue(result.isPresent());
        verify(vehicleRepository, times(1)).findByLicensePlate(plate);
    }

    @Test
    void testFindByRenavam() {
        String renavam = "12345678900";
        Vehicle vehicle = new Vehicle();
        when(vehicleRepository.findByRenavam(renavam)).thenReturn(Optional.of(vehicle));

        Optional<Vehicle> result = vehicleService.findByRenavam(renavam);

        assertTrue(result.isPresent());
        verify(vehicleRepository, times(1)).findByRenavam(renavam);
    }

    @Test
    void testExistsByLicensePlate() {
        String plate = "XYZ1234";
        when(vehicleRepository.existsByLicensePlate(plate)).thenReturn(true);

        boolean exists = vehicleService.existsByLicensePlate(plate);

        assertTrue(exists);
        verify(vehicleRepository, times(1)).existsByLicensePlate(plate);
    }

    @Test
    void testExistsByRenavam() {
        String renavam = "98765432100";
        when(vehicleRepository.existsByRenavam(renavam)).thenReturn(false);

        boolean exists = vehicleService.existsByRenavam(renavam);

        assertFalse(exists);
        verify(vehicleRepository, times(1)).existsByRenavam(renavam);
    }

    @Test
    void testDeleteById() {
        Long id = 1L;

        vehicleService.deleteById(id);

        verify(vehicleRepository, times(1)).deleteById(id);
    }
}
