package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.model.Veiculo;
import br.com.express_frete.fretesexpress.repository.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository veiculoRepository;

    public Veiculo salvar(Veiculo veiculo) {
        return veiculoRepository.save(veiculo);
    }

    public Optional<Veiculo> buscarPorId(Long id) {
        return veiculoRepository.findById(id);
    }

    public Optional<Veiculo> buscarPorPlaca(String placa) {
        return veiculoRepository.findByPlaca(placa);
    }

    public Optional<Veiculo> buscarPorRenavam(String renavam) {
        return veiculoRepository.findByRenavam(renavam);
    }

    public boolean existePlaca(String placa) {
        return veiculoRepository.existsByPlaca(placa);
    }

    public boolean existeRenavam(String renavam) {
        return veiculoRepository.existsByRenavam(renavam);
    }

    public void excluir(Long id) {
        veiculoRepository.deleteById(id);
    }
}