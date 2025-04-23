package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.dto.MotoristaDTO;
import br.com.express_frete.fretesexpress.model.Motorista;
import br.com.express_frete.fretesexpress.service.MotoristaService;
import br.com.express_frete.fretesexpress.service.VeiculoService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/motorista")
public class MotoristaController {

    private static final Logger logger = LoggerFactory.getLogger(MotoristaController.class);

    @Autowired
    private MotoristaService motoristaService;

    @Autowired
    private VeiculoService veiculoService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<?> cadastrar(@Valid @RequestBody MotoristaDTO motoristaDTO) {
        logger.info("Tentando cadastrar motorista: {}", motoristaDTO.getNomeCompleto());
        Motorista motorista = modelMapper.map(motoristaDTO, Motorista.class);
        if (motoristaService.existeCpfCnpj(motorista.getCpfCnpj())) {
            logger.warn("CPF/CNPJ {} ja cadastrado", motorista.getCpfCnpj());
            return ResponseEntity.badRequest().body("CPF/CNPJ ja cadastrado no sistema");
        }
        if (motoristaService.existeEmail(motorista.getEmail())) {
            logger.warn("E-mail {} ja cadastrado", motorista.getEmail());
            return ResponseEntity.badRequest().body("E-mail ja cadastrado no sistema");
        }
        if (motoristaService.existeCnh(motorista.getCnh())) {
            logger.warn("CNH {} ja cadastrada", motorista.getCnh());
            return ResponseEntity.badRequest().body("CNH ja cadastrada no sistema");
        }
        if (motorista.getVeiculos() != null) {
            for (var veiculo : motorista.getVeiculos()) {
                if (veiculoService.existePlaca(veiculo.getPlaca())) {
                    logger.warn("Placa {} ja cadastrada", veiculo.getPlaca());
                    return ResponseEntity.badRequest().body("Placa " + veiculo.getPlaca() + " ja cadastrada");
                }
                if (veiculoService.existeRenavam(veiculo.getRenavam())) {
                    logger.warn("Renavam {} ja cadastrado", veiculo.getRenavam());
                    return ResponseEntity.badRequest().body("Renavam " + veiculo.getRenavam() + " ja cadastrado");
                }
            }
        }
        logger.info("Salvando novo motorista");
        Motorista novoMotorista = motoristaService.salvar(motorista);
        MotoristaDTO novoMotoristaDTO = modelMapper.map(novoMotorista, MotoristaDTO.class);
        logger.info("Motorista salvo com ID: {}", novoMotoristaDTO.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(novoMotoristaDTO);
    }

    @GetMapping
    public ResponseEntity<List<MotoristaDTO>> listarTodos() {
        List<Motorista> motoristas = motoristaService.listarTodos();
        List<MotoristaDTO> motoristasDTO = motoristas.stream()
                .map(motorista -> modelMapper.map(motorista, MotoristaDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(motoristasDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MotoristaDTO> buscarPorId(@PathVariable Long id) {
        Optional<Motorista> motorista = motoristaService.buscarPorId(id);
        return motorista.map(value -> ResponseEntity.ok(modelMapper.map(value, MotoristaDTO.class)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/cpf-cnpj/{cpfCnpj}")
    public ResponseEntity<MotoristaDTO> buscarPorCpfCnpj(@PathVariable String cpfCnpj) {
        Optional<Motorista> motorista = motoristaService.buscarPorCpfCnpj(cpfCnpj);
        return motorista.map(value -> ResponseEntity.ok(modelMapper.map(value, MotoristaDTO.class)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MotoristaDTO> atualizar(@PathVariable Long id, @Valid @RequestBody MotoristaDTO motoristaDTO) {
        Optional<Motorista> motoristaExistente = motoristaService.buscarPorId(id);
        if (motoristaExistente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Motorista motorista = modelMapper.map(motoristaDTO, Motorista.class);
        motorista.setId(id);
        Motorista atualizado = motoristaService.salvar(motorista);
        return ResponseEntity.ok(modelMapper.map(atualizado, MotoristaDTO.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        Optional<Motorista> motorista = motoristaService.buscarPorId(id);
        if (motorista.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        motoristaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}