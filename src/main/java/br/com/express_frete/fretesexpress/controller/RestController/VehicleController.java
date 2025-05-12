package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.model.Vehicle;
import br.com.express_frete.fretesexpress.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService veiculoService;

    @PostMapping
    public ResponseEntity<?> cadastrar(@Valid @RequestBody Vehicle veiculo) {
        if (veiculoService.existePlaca(veiculo.getLicensePlate())) {
            return ResponseEntity.badRequest().body("Placa ja cadastrada no sistema");
        }
        if (veiculoService.existeRenavam(veiculo.getRenavam())) {
            return ResponseEntity.badRequest().body("Renavam ja cadastrado no sistema");
        }
        Vehicle novoVeiculo = veiculoService.salvar(veiculo);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoVeiculo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> buscarPorId(@PathVariable Long id) {
        return veiculoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/plate/{plate}")
    public ResponseEntity<Vehicle> buscarPorPlaca(@PathVariable String plate) {
        return veiculoService.buscarPorPlaca(plate)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody Vehicle veiculo) {
        Optional<Vehicle> veiculoExistente = veiculoService.buscarPorId(id);
        if (veiculoExistente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Vehicle> veiculoPlaca = veiculoService.buscarPorPlaca(veiculo.getLicensePlate());
        if (veiculoPlaca.isPresent() && !veiculoPlaca.get().getId().equals(id)) {
            return ResponseEntity.badRequest().body("Placa ja cadastrada para outro veiculo");
        }

        Optional<Vehicle> veiculoRenavam = veiculoService.buscarPorRenavam(veiculo.getRenavam());
        if (veiculoRenavam.isPresent() && !veiculoRenavam.get().getId().equals(id)) {
            return ResponseEntity.badRequest().body("Renavam ja cadastrado para outro veiculo");
        }

        veiculo.setId(id);
        Vehicle veiculoAtualizado = veiculoService.salvar(veiculo);
        return ResponseEntity.ok(veiculoAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!veiculoService.buscarPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        veiculoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}