package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.model.Vehicle;
import br.com.express_frete.fretesexpress.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;
    
    @GetMapping
    public ResponseEntity<List<Vehicle>> findAll(){
        List<Vehicle> vehicles = vehicleService.findAll();
        if (vehicles.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Vehicle>> findByUserId(@PathVariable Long userId) {
        List<Vehicle> vehicles = vehicleService.findByUserId(userId);
        if (vehicles.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(vehicles);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Vehicle vehicle) {
        if (vehicleService.existsByLicensePlate(vehicle.getLicensePlate())) {
            return ResponseEntity.badRequest().body("Placa ja cadastrada no sistema");
        }
        if (vehicleService.existsByRenavam(vehicle.getRenavam())) {
            return ResponseEntity.badRequest().body("Renavam ja cadastrado no sistema");
        }
        Vehicle newVehicle = vehicleService.save(vehicle);
        return ResponseEntity.status(HttpStatus.CREATED).body(newVehicle);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> findById(@PathVariable Long id) {
        return vehicleService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/plate/{plate}")
    public ResponseEntity<Vehicle> findByLicensePlate(@PathVariable String plate) {
        return vehicleService.findByLicensePlate(plate)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody Vehicle vehicle) {
        Optional<Vehicle> vehicleExistente = vehicleService.findById(id);
        if (vehicleExistente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Vehicle> vehiclePlate = vehicleService.findByLicensePlate(vehicle.getLicensePlate());
        if (vehiclePlate.isPresent() && !vehiclePlate.get().getId().equals(id)) {
            return ResponseEntity.badRequest().body("Placa ja cadastrada para outro veiculo");
        }

        Optional<Vehicle> vehicleRenavam = vehicleService.findByRenavam(vehicle.getRenavam());
        if (vehicleRenavam.isPresent() && !vehicleRenavam.get().getId().equals(id)) {
            return ResponseEntity.badRequest().body("Renavam ja cadastrado para outro veiculo");
        }

        vehicle.setId(id);
        Vehicle updatedVehicle = vehicleService.save(vehicle);
        return ResponseEntity.ok(updatedVehicle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (!vehicleService.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        vehicleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}