package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.model.Frete;
import br.com.express_frete.fretesexpress.repository.FreteRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fretes")
public class FreteController {

    @Autowired
    private FreteRepository repository;

    @GetMapping
    public List<Frete> listar() {
        return repository.findAll();
    }

    @PostMapping
    public Frete cadastrar(@RequestBody @Valid Frete frete) {
        return repository.save(frete);
    }

    @GetMapping("/{id}")
    public Frete buscar(@PathVariable Long id) {
        return repository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public Frete atualizar(@PathVariable Long id, @RequestBody @Valid Frete dados) {
        return repository.findById(id).map(frete -> {
            frete.setNomeProduto(dados.getNomeProduto());
            frete.setPeso(dados.getPeso());
            frete.setAltura(dados.getAltura());
            frete.setLargura(dados.getLargura());
            frete.setComprimento(dados.getComprimento());
            frete.setCidadeOrigem(dados.getCidadeOrigem());
            frete.setEstadoOrigem(dados.getEstadoOrigem());
            frete.setCidadeDestino(dados.getCidadeDestino());
            frete.setEstadoDestino(dados.getEstadoDestino());
            frete.setDataInicial(dados.getDataInicial());
            frete.setDataFinal(dados.getDataFinal());
            return repository.save(frete);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
