package br.com.express_frete.fretesexpress.controller;

import br.com.express_frete.fretesexpress.dto.ContractDTO;
import br.com.express_frete.fretesexpress.model.Contract;
import br.com.express_frete.fretesexpress.service.ContractService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {

    @Autowired
    private ContractService contractService;

    @GetMapping
    public ResponseEntity<List<Contract>> getAllContracts() {
        return ResponseEntity.ok(contractService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Contract> getContractById(@PathVariable Long id) {
        return ResponseEntity.ok(contractService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Contract> createContract(@Valid @RequestBody ContractDTO contractDTO) {
        Contract createdContract = contractService.create(contractDTO);
        return new ResponseEntity<>(createdContract, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contract> updateContract(@PathVariable Long id, @Valid @RequestBody ContractDTO contractDTO) {
        Contract updatedContract = contractService.update(id, contractDTO);
        return ResponseEntity.ok(updatedContract);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContract(@PathVariable Long id) {
        contractService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<Contract>> getContractsByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(contractService.findByClient(clientId));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<Contract>> getContractsByDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(contractService.findByDriver(driverId));
    }

    @GetMapping("/freight/{freightId}")
    public ResponseEntity<List<Contract>> getContractsByFreight(@PathVariable Long freightId) {
        return ResponseEntity.ok(contractService.findByFreight(freightId));
    }
} 