package br.com.express_frete.fretesexpress.repository;

import br.com.express_frete.fretesexpress.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByLicensePlate(String placa);

    boolean existsByLicensePlate(String placa);

    Optional<Vehicle> findByRenavam(String renavam);

    boolean existsByRenavam(String renavam);
}