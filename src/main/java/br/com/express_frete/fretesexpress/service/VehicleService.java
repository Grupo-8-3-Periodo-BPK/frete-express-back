package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.model.Vehicle;
import br.com.express_frete.fretesexpress.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<Vehicle> findAll(){
        return vehicleRepository.findAll();
    }

    public Vehicle save(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> findByUserId(Long userId) {
        return vehicleRepository.findByUserId(userId);
    }

    public Optional<Vehicle> findById(Long id) {
        return vehicleRepository.findById(id);
    }

    public Optional<Vehicle> findByLicensePlate(String placa) {
        return vehicleRepository.findByLicensePlate(placa);
    }

    public Optional<Vehicle> findByRenavam(String renavam) {
        return vehicleRepository.findByRenavam(renavam);
    }

    public boolean existsByLicensePlate(String placa) {
        return vehicleRepository.existsByLicensePlate(placa);
    }

    public boolean existsByRenavam(String renavam) {
        return vehicleRepository.existsByRenavam(renavam);
    }

    public void deleteById(Long id) {
        vehicleRepository.deleteById(id);
    }
}