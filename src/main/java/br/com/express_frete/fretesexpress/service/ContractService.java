package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.dto.ContractDTO;
import br.com.express_frete.fretesexpress.model.Contract;
import br.com.express_frete.fretesexpress.model.Freight;
import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.repository.ContractRepository;
import br.com.express_frete.fretesexpress.repository.FreightRepository;
import br.com.express_frete.fretesexpress.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContractService {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FreightRepository freightRepository;

    public List<Contract> findAll() {
        return contractRepository.findAll();
    }

    public Contract findById(Long id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found with id: " + id));
    }

    public Contract create(ContractDTO contractDTO) {
        Contract contract = new Contract();
        return saveContract(contract, contractDTO);
    }

    public Contract update(Long id, ContractDTO contractDTO) {
        Contract contract = findById(id);
        return saveContract(contract, contractDTO);
    }

    private Contract saveContract(Contract contract, ContractDTO dto) {
        contract.setName(dto.getName());

        User client = userRepository.findById(dto.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Client not found with id: " + dto.getClientId()));
        contract.setClient(client);

        User driver = userRepository.findById(dto.getDriverId())
                .orElseThrow(() -> new EntityNotFoundException("Driver not found with id: " + dto.getDriverId()));
        contract.setDriver(driver);

        Freight freight = freightRepository.findById(dto.getFreightId())
                .orElseThrow(() -> new EntityNotFoundException("Freight not found with id: " + dto.getFreightId()));
        contract.setFreight(freight);

        contract.setDriverAccepted(dto.getDriverAccepted());
        contract.setClientAccepted(dto.getClientAccepted());

        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + dto.getUserId()));
            contract.setUser(user);
        }

        return contractRepository.save(contract);
    }

    public void delete(Long id) {
        Contract contract = findById(id);
        contractRepository.delete(contract);
    }

    public List<Contract> findByClient(Long clientId) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found with id: " + clientId));
        return contractRepository.findByClient(client);
    }

    public List<Contract> findByDriver(Long driverId) {
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found with id: " + driverId));
        return contractRepository.findByDriver(driver);
    }

    public List<Contract> findByFreight(Long freightId) {
        Freight freight = freightRepository.findById(freightId)
                .orElseThrow(() -> new EntityNotFoundException("Freight not found with id: " + freightId));
        return contractRepository.findByFreight(freight);
    }
}