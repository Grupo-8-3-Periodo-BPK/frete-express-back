package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.dto.FormularioSuporteDTO;
import br.com.express_frete.fretesexpress.model.FormularioSuporte;
import br.com.express_frete.fretesexpress.service.FormularioSuporteService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/suporte")
public class FormularioSuporteController {

    private static final Logger logger = LoggerFactory.getLogger(FormularioSuporteController.class);

    @Autowired
    private FormularioSuporteService formularioSuporteService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/enviar")
    public ResponseEntity<?> enviar(@Valid @RequestBody FormularioSuporteDTO formularioDTO) {
        logger.info("Recebendo formulario de suporte de: {}", formularioDTO.getNomeSolicitante());
        FormularioSuporte formulario = modelMapper.map(formularioDTO, FormularioSuporte.class);
        FormularioSuporte salvo = formularioSuporteService.salvar(formulario);
        FormularioSuporteDTO salvoDTO = modelMapper.map(salvo, FormularioSuporteDTO.class);
        logger.info("Formulario salvo com ID: {}", salvoDTO.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(salvoDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormularioSuporteDTO> buscarPorId(@PathVariable Long id) {
        Optional<FormularioSuporte> formulario = formularioSuporteService.buscarPorId(id);
        return formulario.map(value -> ResponseEntity.ok(modelMapper.map(value, FormularioSuporteDTO.class)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<FormularioSuporteDTO>> listarTodos() {
        List<FormularioSuporte> formularios = formularioSuporteService.listarTodos();
        List<FormularioSuporteDTO> formulariosDTO = formularios.stream()
                .map(formulario -> modelMapper.map(formulario, FormularioSuporteDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(formulariosDTO);
    }
}