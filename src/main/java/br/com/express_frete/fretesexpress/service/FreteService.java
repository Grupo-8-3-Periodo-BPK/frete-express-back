package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.dto.FreteDTO;
import br.com.express_frete.fretesexpress.model.Frete;
import br.com.express_frete.fretesexpress.model.enums.FreteStatus;
import br.com.express_frete.fretesexpress.repository.FreteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FreteService {

    @Autowired
    private FreteRepository freteRepository;

    // Método que transforma entidade Frete → DTO
    public FreteDTO toDTO(Frete frete) {
        FreteDTO dto = new FreteDTO();
        dto.setId(frete.getId());
        dto.setNomeProduto(frete.getNomeProduto());
        dto.setPeso(frete.getPeso());
        dto.setAltura(frete.getAltura());
        dto.setComprimento(frete.getComprimento());
        dto.setLargura(frete.getLargura());
        dto.setDataInicial(frete.getDataInicial());
        dto.setDataFinal(frete.getDataFinal());
        dto.setCidadeOrigem(frete.getCidadeOrigem());
        dto.setEstadoOrigem(frete.getEstadoOrigem());
        dto.setCidadeDestino(frete.getCidadeDestino());
        dto.setEstadoDestino(frete.getEstadoDestino());
        dto.setTipoCaminhao(frete.getTipoCaminhao());
        dto.setTipoCarga(frete.getTipoCarga());
        dto.setStatus(frete.getStatus());
        return dto;
    }

    // Método que transforma DTO → entidade Frete
    public Frete toEntity(FreteDTO dto) {
        Frete frete = new Frete();
        frete.setId(dto.getId());
        frete.setNomeProduto(dto.getNomeProduto());
        frete.setPeso(dto.getPeso());
        frete.setAltura(dto.getAltura());
        frete.setComprimento(dto.getComprimento());
        frete.setLargura(dto.getLargura());
        frete.setDataInicial(dto.getDataInicial());
        frete.setDataFinal(dto.getDataFinal());
        frete.setCidadeOrigem(dto.getCidadeOrigem());
        frete.setEstadoOrigem(dto.getEstadoOrigem());
        frete.setCidadeDestino(dto.getCidadeDestino());
        frete.setEstadoDestino(dto.getEstadoDestino());
        frete.setTipoCaminhao(dto.getTipoCaminhao());
        frete.setTipoCarga(dto.getTipoCarga());
        frete.setStatus(dto.getStatus());
        return frete;
    }

    // Método para aprovação de frete
    public Frete aprovarFrete(Long idFrete, String aprovador) {
        Frete frete = freteRepository.findById(idFrete)
                .orElseThrow(() -> new RuntimeException("Frete não encontrado"));

        FreteStatus statusAtual = frete.getStatus();

        if (aprovador.equalsIgnoreCase("cliente")) {
            if (statusAtual == FreteStatus.APROVADO_MOTORISTA) {
                frete.setStatus(FreteStatus.CONCLUIDO);
            } else {
                frete.setStatus(FreteStatus.APROVADO_CLIENTE);
            }
        } else if (aprovador.equalsIgnoreCase("motorista")) {
            if (statusAtual == FreteStatus.APROVADO_CLIENTE) {
                frete.setStatus(FreteStatus.CONCLUIDO);
            } else {
                frete.setStatus(FreteStatus.APROVADO_MOTORISTA);
            }
        } else {
            throw new IllegalArgumentException("Aprovador deve ser 'cliente' ou 'motorista'");
        }

        return freteRepository.save(frete);
    }
    //metodo para filtrar frete
    public List<FreteDTO> filtrarFretes(String cidadeOrigem, String cidadeDestino, String tipoCaminhao, String tipoCarga) {
        List<Frete> fretes = freteRepository.buscarPorFiltros(cidadeOrigem, cidadeDestino, tipoCaminhao, tipoCarga);
        return fretes.stream().map(this::toDTO).toList();
    }
}
