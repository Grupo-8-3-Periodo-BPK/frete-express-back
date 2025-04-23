package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.model.Motorista;
import br.com.express_frete.fretesexpress.model.Veiculo;
import br.com.express_frete.fretesexpress.repository.MotoristaRepository;
import br.com.express_frete.fretesexpress.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MotoristaService {

    @Autowired
    private MotoristaRepository motoristaRepository;

    @Autowired
    private VeiculoRepository veiculoRepository;

    public List<Motorista> listarTodos() {
        return motoristaRepository.findAll();
    }

    public Optional<Motorista> buscarPorId(long id) {
        return motoristaRepository.findById(id);
    }

    public Optional<Motorista> buscarPorCpfCnpj(String cpfCnpj) {
        return motoristaRepository.findByCpfCnpj(cpfCnpj);
    }

    public Optional<Motorista> buscarPorEmail(String email) {
        return motoristaRepository.findByEmail(email);
    }

    public Optional<Motorista> buscarPorCnh(String cnh) {
        return motoristaRepository.findByCnh(cnh);
    }

    public Motorista salvar(Motorista motorista) {
        if (motorista.getVeiculos() != null) {
            motorista.getVeiculos().forEach(veiculo -> veiculo.setMotorista(motorista));
        }
        return motoristaRepository.save(motorista);
    }

    public void excluir(Long id) {
        motoristaRepository.deleteById(id);
    }

    public boolean existeCpfCnpj(String cpfCnpj) {
        return motoristaRepository.existsByCpfCnpj(cpfCnpj);
    }

    public boolean existeEmail(String email) {
        return motoristaRepository.existsByEmail(email);
    }

    public boolean existeCnh(String cnh) {
        return motoristaRepository.existsByCnh(cnh);
    }
}