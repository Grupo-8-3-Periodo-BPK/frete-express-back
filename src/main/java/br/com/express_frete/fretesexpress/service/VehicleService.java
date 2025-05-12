package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.model.Vehicle;
import br.com.express_frete.fretesexpress.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository veiculoRepository;

    public Vehicle salvar(Vehicle veiculo) {
        return veiculoRepository.save(veiculo);
    }

    public Optional<Vehicle> buscarPorId(Long id) {
        return veiculoRepository.findById(id);
    }

    public Optional<Vehicle> buscarPorPlaca(String placa) {
        return veiculoRepository.findByLicensePlate(placa);
    }

    public Optional<Vehicle> buscarPorRenavam(String renavam) {
        return veiculoRepository.findByRenavam(renavam);
    }

    public boolean existePlaca(String placa) {
        return veiculoRepository.existsByLicensePlate(placa);
    }

    public boolean existeRenavam(String renavam) {
        return veiculoRepository.existsByRenavam(renavam);
    }

    public void excluir(Long id) {
        veiculoRepository.deleteById(id);
    }
}