package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.dto.FormDTO;
import br.com.express_frete.fretesexpress.model.Form;
import br.com.express_frete.fretesexpress.service.FormService;
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
@RequestMapping("/api/forms")
public class FormController {

    private static final Logger logger = LoggerFactory.getLogger(FormController.class);

    @Autowired
    private FormService formService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/send")
    public ResponseEntity<?> send(@Valid @RequestBody FormDTO formDTO) {
        logger.info("Receiving support form from: {}", formDTO.getNameRequester());
        Form form = modelMapper.map(formDTO, Form.class);
        Form saved = formService.save(form);
        FormDTO savedDTO = modelMapper.map(saved, FormDTO.class);
        logger.info("Form saved with ID: {}", savedDTO.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormDTO> findById(@PathVariable Long id) {
        Optional<Form> form = formService.findById(id);
        return form.map(value -> ResponseEntity.ok(modelMapper.map(value, FormDTO.class)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<FormDTO>> findAll() {
        List<Form> forms = formService.findAll();
        List<FormDTO> formsDTO = forms.stream()
                .map(form -> modelMapper.map(form, FormDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(formsDTO);
    }
}