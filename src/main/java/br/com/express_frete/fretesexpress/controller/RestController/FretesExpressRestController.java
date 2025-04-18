package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.model.FretesExpress;
import br.com.express_frete.fretesexpress.repository.FretesExpressRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fretes")
public class FretesExpressRestController {

    @Autowired
    private FretesExpressRepository repository;

    @GetMapping
    public List<FretesExpress> listar() {
        return repository.findAll();
    }

    @PostMapping
    public FretesExpress cadastrar(@RequestBody @Valid FretesExpress frete) {
        return repository.save(frete);
    }

    @GetMapping("/{id}")
    public FretesExpress buscar(@PathVariable Long id) {
        return repository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public FretesExpress atualizar(@PathVariable Long id, @RequestBody @Valid FretesExpress dados) {
        return repository.findById(id).map(frete -> {
            frete.setOrigem(dados.getOrigem());
            frete.setDestino(dados.getDestino());
            frete.setDataEntrega(dados.getDataEntrega());
            frete.setPeso(dados.getPeso());
            frete.setValor(dados.getValor());
            return repository.save(frete);
        }).orElse(null);
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
