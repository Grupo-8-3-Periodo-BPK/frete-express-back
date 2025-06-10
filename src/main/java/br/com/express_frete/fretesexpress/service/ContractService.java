package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.dto.ContractDTO;
import br.com.express_frete.fretesexpress.model.Contract;
import br.com.express_frete.fretesexpress.model.Freight;
import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.model.Vehicle;
import br.com.express_frete.fretesexpress.model.Tracking;
import br.com.express_frete.fretesexpress.repository.ContractRepository;
import br.com.express_frete.fretesexpress.repository.FreightRepository;
import br.com.express_frete.fretesexpress.repository.UserRepository;
import br.com.express_frete.fretesexpress.repository.VehicleRepository;
import br.com.express_frete.fretesexpress.repository.TrackingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContractService {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FreightRepository freightRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private TrackingRepository trackingRepository;

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
        User client = userRepository.findById(dto.getClient_id())
                .orElseThrow(() -> new EntityNotFoundException("Client not found with id: " + dto.getClient_id()));
        contract.setClient(client);

        User driver = userRepository.findById(dto.getDriver_id())
                .orElseThrow(() -> new EntityNotFoundException("Driver not found with id: " + dto.getDriver_id()));
        contract.setDriver(driver);

        Freight freight = freightRepository.findById(dto.getFreight_id())
                .orElseThrow(() -> new EntityNotFoundException("Freight not found with id: " + dto.getFreight_id()));
        contract.setFreight(freight);

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicle_id())
                .orElseThrow(() -> new EntityNotFoundException("Vehicle not found with id: " + dto.getVehicle_id()));
        contract.setVehicle(vehicle);

        contract.setPickupDate(dto.getPickup_date());
        contract.setDeliveryDate(dto.getDelivery_date());
        contract.setAgreedValue(dto.getAgreed_value());

        contract.setDriverAccepted(dto.getDriver_accepted());
        contract.setClientAccepted(dto.getClient_accepted());

        Contract savedContract = contractRepository.save(contract);
        dto.setDisplay_name(savedContract.getDisplayName());
        return savedContract;
    }

    @Transactional
    public Contract setDriverAcceptance(Long id, boolean accepted) {
        Contract contract = findById(id);
        contract.setDriverAccepted(accepted);
        return contractRepository.save(contract);
    }

    @Transactional
    public Contract setClientAcceptance(Long id, boolean accepted) {
        Contract contract = findById(id);
        contract.setClientAccepted(accepted);
        return contractRepository.save(contract);
    }

    public void delete(Long id) {
        Contract contract = findById(id);

        List<Tracking> trackings = trackingRepository.findByContract(contract);
        trackingRepository.deleteAll(trackings);

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