package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.dto.OrcamentoFreteDTO;
import br.com.express_frete.fretesexpress.model.OrcamentoFrete;
import br.com.express_frete.fretesexpress.model.enums.CategoriaVeiculo;
import br.com.express_frete.fretesexpress.repository.OrcamentoFreteRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrcamentoFreteService {

    private static final Logger logger = LoggerFactory.getLogger(OrcamentoFreteService.class);

    @Autowired
    private OrcamentoFreteRepository orcamentoFreteRepository;

    @Autowired
    private ModelMapper modelMapper;

    public OrcamentoFreteDTO solicitarOrcamento(OrcamentoFreteDTO orcamentoDTO) {
        logger.info("Solicitando orçamento para carga: {}", orcamentoDTO.getTipoCarga());

        // Mapear DTO para entidade
        OrcamentoFrete orcamento = modelMapper.map(orcamentoDTO, OrcamentoFrete.class);

        // Definir dataSolicitacao explicitamente
        orcamento.setDataSolicitacao(LocalDateTime.now());
        logger.debug("Data de solicitaçao definida: {}", orcamento.getDataSolicitacao());

        // Calcular valor estimado
        double valorEstimado = calcularOrcamento(orcamento);
        orcamento.setValorEstimado(valorEstimado);
        logger.debug("Valor estimado calculado: {}", valorEstimado);

        // Salvar orçamento
        OrcamentoFrete orcamentoSalvo = salvar(orcamento);
        return modelMapper.map(orcamentoSalvo, OrcamentoFreteDTO.class);
    }

    public OrcamentoFrete salvar(OrcamentoFrete orcamento) {
        logger.info("Salvando orçamento para carga: {}", orcamento.getTipoCarga());
        if (orcamento.getDataSolicitacao() == null) {
            logger.warn("dataSolicitacao esta nulo antes de salvar, definindo valor padrao");
            orcamento.setDataSolicitacao(LocalDateTime.now());
        }
        if (orcamento.getValorEstimado() == null) {
            logger.warn("valorEstimado esta nulo antes de salvar, recalculando");
            orcamento.setValorEstimado(calcularOrcamento(orcamento));
        }
        return orcamentoFreteRepository.save(orcamento);
    }

    private double calcularOrcamento(OrcamentoFrete orcamento) {
        logger.info("Calculando orçamento para carga: {}", orcamento.getTipoCarga());

        // Validar campos obrigatorios para calculo
        if (orcamento.getCidadeOrigem() == null || orcamento.getEstadoOrigem() == null ||
                orcamento.getCidadeDestino() == null || orcamento.getEstadoDestino() == null ||
                orcamento.getTipoVeiculo() == null) {
            logger.error("Campos obrigatorios para calculo estao nulos: {}", orcamento);
            throw new IllegalArgumentException("Campos obrigatorios para calculo do orçamento estao ausentes");
        }

        // Calcular distância (simulada)
        double distanciaKm = calcularDistancia(
                orcamento.getCidadeOrigem(), orcamento.getEstadoOrigem(),
                orcamento.getCidadeDestino(), orcamento.getEstadoDestino()
        );

        // Definir consumo (litros/km) e custo por litro (R$/litro) por tipo de veículo
        double consumoLitrosPorKm;
        double custoPorLitro = 6.00; // Fixo em R$6,00 por litro

        switch (orcamento.getTipoVeiculo()) {
            case CAMINHAO:
                consumoLitrosPorKm = 0.5;
                break;
            case CARRETA:
                consumoLitrosPorKm = 0.7;
                break;
            case VAN:
                consumoLitrosPorKm = 0.2;
                break;
            case UTILITARIO:
                consumoLitrosPorKm = 0.1;
                break;
            default:
                logger.error("Tipo de veículo invalido: {}", orcamento.getTipoVeiculo());
                throw new IllegalArgumentException("Tipo de veículo invalido: " + orcamento.getTipoVeiculo());
        }

        // Formula: Distância × Consumo (litros/km) × Custo por litro
        double valorEstimado = distanciaKm * consumoLitrosPorKm * custoPorLitro;

        logger.info("Orçamento calculado: R${} (Distância: {}km, Consumo: {}L/km, Custo por litro: R${})",
                String.format("%.2f", valorEstimado), distanciaKm, consumoLitrosPorKm, custoPorLitro);
        return valorEstimado;
    }

    private double calcularDistancia(String cidadeOrigem, String estadoOrigem, String cidadeDestino, String estadoDestino) {
        logger.info("Simulando calculo de distância entre {} ({}) e {} ({})",
                cidadeOrigem, estadoOrigem, cidadeDestino, estadoDestino);

        // Simulaçao de distância
        if (cidadeOrigem == null || estadoOrigem == null || cidadeDestino == null || estadoDestino == null) {
            logger.error("Campos de origem ou destino estao nulos");
            throw new IllegalArgumentException("Campos de origem ou destino estao ausentes");
        }

        if (cidadeOrigem.equalsIgnoreCase(cidadeDestino) && estadoOrigem.equalsIgnoreCase(estadoDestino)) {
            return 10.0; // Mesma cidade
        }
        return estadoOrigem.equalsIgnoreCase(estadoDestino) ? 100.0 : 500.0; // Mesmo estado ou estados diferentes
    }
}