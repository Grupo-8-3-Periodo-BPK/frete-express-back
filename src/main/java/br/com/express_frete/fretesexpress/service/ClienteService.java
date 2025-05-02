package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.model.Cliente;
import br.com.express_frete.fretesexpress.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente cadastrarCliente(Cliente cliente) {
        if (clienteRepository.existsByCpfCnpj(cliente.getCpfcnpj())) {
            throw new RuntimeException("CPF ou CNPJ já cadastrado.");
        }
        return clienteRepository.save(cliente);
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    public static Cliente buscarPorId(Long IdCliente) {
        return clienteRepository.findById(IdCliente)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));
    }
}
