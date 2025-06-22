package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.dto.ContractDTO;
import br.com.express_frete.fretesexpress.dto.ContractResponseDTO;
import br.com.express_frete.fretesexpress.model.Contract;
import br.com.express_frete.fretesexpress.model.enums.Status;
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
    public ResponseEntity<List<ContractResponseDTO>> getAllContracts() {
        return ResponseEntity.ok(contractService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractResponseDTO> getContractById(@PathVariable Long id) {
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
    public ResponseEntity<List<ContractResponseDTO>> getContractsByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(contractService.findByClient(clientId));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<ContractResponseDTO>> getContractsByDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(contractService.findByDriver(driverId));
    }

    @GetMapping("/freight/{freightId}")
    public ResponseEntity<List<ContractResponseDTO>> getContractsByFreight(@PathVariable Long freightId) {
        return ResponseEntity.ok(contractService.findByFreight(freightId));
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<ContractResponseDTO> approveContract(@PathVariable Long id) {
        ContractResponseDTO updatedContract = contractService.approveContract(id);
        return ResponseEntity.ok(updatedContract);
    }

    @PatchMapping("/{id}/cancel-by-driver")
    public ResponseEntity<ContractResponseDTO> cancelByDriver(@PathVariable Long id) {
        ContractResponseDTO updatedContract = contractService.updateStatus(id, Status.CANCELLED_BY_DRIVER);
        return ResponseEntity.ok(updatedContract);
    }

    @PatchMapping("/{id}/cancel-by-client")
    public ResponseEntity<ContractResponseDTO> cancelByClient(@PathVariable Long id) {
        ContractResponseDTO updatedContract = contractService.updateStatus(id, Status.CANCELLED_BY_CLIENT);
        return ResponseEntity.ok(updatedContract);
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<ContractResponseDTO> completeContract(@PathVariable Long id) {
        ContractResponseDTO updatedContract = contractService.updateStatus(id, Status.COMPLETED);
        return ResponseEntity.ok(updatedContract);
    }

    @PatchMapping("/{id}/start")
    public ResponseEntity<ContractResponseDTO> startContract(@PathVariable Long id) {
        ContractResponseDTO updatedContract = contractService.updateStatus(id, Status.IN_PROGRESS);
        return ResponseEntity.ok(updatedContract);
    }
}