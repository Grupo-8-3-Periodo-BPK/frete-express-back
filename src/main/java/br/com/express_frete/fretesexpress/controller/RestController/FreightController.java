package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.dto.FreightRequestDTO;
import br.com.express_frete.fretesexpress.dto.FreightUpdateDTO;
import br.com.express_frete.fretesexpress.model.Freight;
import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.model.enums.FreightStatus;
import br.com.express_frete.fretesexpress.model.enums.Status;
import br.com.express_frete.fretesexpress.repository.ContractRepository;
import br.com.express_frete.fretesexpress.repository.FreightRepository;
import br.com.express_frete.fretesexpress.repository.TrackingRepository;
import br.com.express_frete.fretesexpress.service.FreightService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/freights")
public class FreightController {

    @Autowired
    private FreightRepository repository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private TrackingRepository trackingRepository;

    @Autowired
    private FreightService freightService;

    @GetMapping
    public ResponseEntity<List<Freight>> getAllFreights(@RequestParam(required = false) FreightStatus status) {
        List<Freight> freights;
        if (status != null) {
            freights = freightService.findByStatus(status);
        } else {
            freights = freightService.findAll();
        }
        return ResponseEntity.ok(freights);
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
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado.");
        }
        User user = (User) auth.getPrincipal();

        return repository.findById(id).map(freight -> {
            if (!freight.getUserId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Você não tem permissão para excluir este frete.");
            }

            var contracts = contractRepository.findByFreight(freight);
            boolean hasActiveOrCompletedContracts = contracts.stream()
                    .anyMatch(c -> c.getStatus() == Status.ACTIVE || c.getStatus() == Status.COMPLETED);

            if (hasActiveOrCompletedContracts) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("Não é possível excluir este frete, pois ele possui contratos ativos ou concluídos.");
            }

            // Para cada contrato pendente/rejeitado, exclui os trackings associados
            contracts.forEach(contract -> {
                var trackings = trackingRepository.findByContract(contract);
                trackingRepository.deleteAll(trackings);
            });

            // Exclui os contratos pendentes/rejeitados
            contractRepository.deleteAll(contracts);
            // Exclui o frete
            repository.delete(freight);

            return ResponseEntity.noContent().build(); // 204

        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Frete não encontrado."));
    }
}
