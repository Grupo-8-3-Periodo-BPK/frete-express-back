package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.model.Freight;
import br.com.express_frete.fretesexpress.repository.FreightRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/freights")
public class FreightController {

    @Autowired
    private FreightRepository repository;

    @GetMapping
    public List<Freight> listar() {
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody @Valid Freight freight, HttpServletRequest request) {
        // Verificar a autenticação atual para logs
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Criando frete: " + freight.getName() + " - Usuário: " + (auth != null ? auth.getName() : "Não autenticado"));
        
        // Salvar o frete
        Freight savedFreight = repository.save(freight);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFreight);
    }

    @GetMapping("/{id}")
    public Freight buscar(@PathVariable Long id) {
        return repository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Freight atualizar(@PathVariable Long id, @RequestBody @Valid Freight dados) {
        return repository.findById(id).map(frete -> {
            frete.setName(dados.getName());
            frete.setWeight(dados.getWeight());
            frete.setHeight(dados.getHeight());
            frete.setWidth(dados.getWidth());
            frete.setLength(dados.getLength());
            frete.setOrigin_city(dados.getOrigin_city());
            frete.setOrigin_state(dados.getOrigin_state());
            frete.setDestination_city(dados.getDestination_city());
            frete.setDestination_state(dados.getDestination_state());
            frete.setInitial_date(dados.getInitial_date());
            frete.setFinal_date(dados.getFinal_date());
            return repository.save(frete);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
