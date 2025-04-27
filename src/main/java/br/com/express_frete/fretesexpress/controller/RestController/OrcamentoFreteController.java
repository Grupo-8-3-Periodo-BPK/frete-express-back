package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.dto.OrcamentoFreteDTO;
import br.com.express_frete.fretesexpress.model.OrcamentoFrete;
import br.com.express_frete.fretesexpress.service.OrcamentoFreteService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orcamentos")
public class OrcamentoFreteController {

    private static final Logger logger = LoggerFactory.getLogger(OrcamentoFreteController.class);

    @Autowired
    private OrcamentoFreteService orcamentoFreteService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<?> solicitarOrcamento(@Valid @RequestBody OrcamentoFreteDTO orcamentoDTO) {
        logger.info("Recebendo solicitacao de orcamento para carga: {}", orcamentoDTO.getTipoCarga());
        OrcamentoFrete orcamento = modelMapper.map(orcamentoDTO, OrcamentoFrete.class);
        OrcamentoFrete salvo = orcamentoFreteService.salvar(orcamento);
        OrcamentoFreteDTO salvoDTO = modelMapper.map(salvo, OrcamentoFreteDTO.class);
        logger.info("Orcamento salvo com ID: {}", salvoDTO.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(salvoDTO);
    }
}