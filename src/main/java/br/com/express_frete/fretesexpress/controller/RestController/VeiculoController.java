package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.model.Veiculo;
import br.com.express_frete.fretesexpress.service.VeiculoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/veiculos")
public class VeiculoController {

    @Autowired
    private VeiculoService veiculoService;

    @PostMapping
    public ResponseEntity<?> cadastrar(@Valid @RequestBody Veiculo veiculo) {
        if (veiculoService.existePlaca(veiculo.getPlaca())) {
            return ResponseEntity.badRequest().body("Placa ja cadastrada no sistema");
        }
        if (veiculoService.existeRenavam(veiculo.getRenavam())) {
            return ResponseEntity.badRequest().body("Renavam ja cadastrado no sistema");
        }
        Veiculo novoVeiculo = veiculoService.salvar(veiculo);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoVeiculo);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Veiculo> buscarPorId(@PathVariable Long id) {
        return veiculoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/placa/{placa}")
    public ResponseEntity<Veiculo> buscarPorPlaca(@PathVariable String placa) {
        return veiculoService.buscarPorPlaca(placa)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @Valid @RequestBody Veiculo veiculo) {
        Optional<Veiculo> veiculoExistente = veiculoService.buscarPorId(id);
        if (veiculoExistente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Optional<Veiculo> veiculoPlaca = veiculoService.buscarPorPlaca(veiculo.getPlaca());
        if (veiculoPlaca.isPresent() && !veiculoPlaca.get().getId().equals(id)) {
            return ResponseEntity.badRequest().body("Placa ja cadastrada para outro veiculo");
        }

        Optional<Veiculo> veiculoRenavam = veiculoService.buscarPorRenavam(veiculo.getRenavam());
        if (veiculoRenavam.isPresent() && !veiculoRenavam.get().getId().equals(id)) {
            return ResponseEntity.badRequest().body("Renavam ja cadastrado para outro veiculo");
        }

        veiculo.setId(id);
        Veiculo veiculoAtualizado = veiculoService.salvar(veiculo);
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