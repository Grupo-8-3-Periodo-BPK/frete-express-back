package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.dto.FreightRequestDTO;
import br.com.express_frete.fretesexpress.dto.FreightUpdateDTO;
import br.com.express_frete.fretesexpress.model.Freight;
import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.repository.FreightRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/freights")
public class FreightController {

    @Autowired
    private FreightRepository repository;

    @GetMapping
    public List<Freight> getAll() {
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> register(@RequestBody @Valid FreightRequestDTO freightDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof User)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuário não autenticado ou tipo de principal inválido.");
        }

        User user = (User) auth.getPrincipal();

        Freight freight = new Freight();
        freight.setName(freightDTO.getName());
        freight.setPrice(freightDTO.getPrice());
        freight.setWeight(freightDTO.getWeight());
        freight.setHeight(freightDTO.getHeight());
        freight.setLength(freightDTO.getLength());
        freight.setWidth(freightDTO.getWidth());
        freight.setInitial_date(freightDTO.getInitial_date());
        freight.setFinal_date(freightDTO.getFinal_date());
        freight.setOrigin_city(freightDTO.getOrigin_city());
        freight.setOrigin_state(freightDTO.getOrigin_state());
        freight.setDestination_city(freightDTO.getDestination_city());
        freight.setDestination_state(freightDTO.getDestination_state());

        // Atribui o ID do usuário autenticado
        freight.setUserId(user.getId());

        Freight savedFreight = repository.save(freight);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedFreight);
    }

    @GetMapping("/{id}")
    public Freight getById(@PathVariable Long id) {
        return repository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody @Valid FreightUpdateDTO freightDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuário não autenticado ou tipo de principal inválido.");
        }
        User user = (User) auth.getPrincipal();

        return repository.findById(id).map(freight -> {
            if (!freight.getUserId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Você não tem permissão para editar este frete.");
            }

            freight.setName(freightDTO.getName());
            freight.setPrice(freightDTO.getPrice());
            freight.setWeight(freightDTO.getWeight());
            freight.setHeight(freightDTO.getHeight());
            freight.setWidth(freightDTO.getWidth());
            freight.setLength(freightDTO.getLength());
            freight.setOrigin_city(freightDTO.getOrigin_city());
            freight.setOrigin_state(freightDTO.getOrigin_state());
            freight.setDestination_city(freightDTO.getDestination_city());
            freight.setDestination_state(freightDTO.getDestination_state());
            freight.setInitial_date(freightDTO.getInitial_date());
            freight.setFinal_date(freightDTO.getFinal_date());

            Freight updatedFreight = repository.save(freight);
            return ResponseEntity.ok(updatedFreight);
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Frete não encontrado."));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        try {
            repository.deleteById(id);
            return ResponseEntity.ok("Frete deletado com sucesso.");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Não é possível excluir este frete pois ele está associado a um contrato existente.");
        }
    }
}
