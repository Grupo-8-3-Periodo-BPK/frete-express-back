package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.model.Cliente;
import br.com.express_frete.fretesexpress.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping
    public Cliente cadastrar(@RequestBody Cliente cliente){
        return clienteService.cadastrarCliente(cliente);
    }

    @GetMapping
    public List<Cliente> listar(){
        return clienteService.listarTodos();
    }

    @GetMapping("/{IdCliente}")
    public Cliente buscar(@PathVariable Long IdCliente){
        return ClienteService.buscarPorId(IdCliente);
    }
}
