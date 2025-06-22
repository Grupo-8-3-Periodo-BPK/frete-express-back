package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.dto.ContractDTO;
import br.com.express_frete.fretesexpress.dto.ContractResponseDTO;
import br.com.express_frete.fretesexpress.model.Contract;
import br.com.express_frete.fretesexpress.model.Freight;
import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.model.Vehicle;
import br.com.express_frete.fretesexpress.model.Tracking;
import br.com.express_frete.fretesexpress.model.enums.FreightStatus;
import br.com.express_frete.fretesexpress.model.enums.Status;
import br.com.express_frete.fretesexpress.repository.ContractRepository;
import br.com.express_frete.fretesexpress.repository.FreightRepository;
import br.com.express_frete.fretesexpress.repository.UserRepository;
import br.com.express_frete.fretesexpress.repository.VehicleRepository;
import br.com.express_frete.fretesexpress.repository.TrackingRepository;
import br.com.express_frete.fretesexpress.service.RouteService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private RouteService routeService;

    private ContractResponseDTO convertToDTO(Contract contract) {
        return new ContractResponseDTO(
                contract.getId(),
                contract.getDisplayName(),
                contract.getAgreedValue(),
                contract.getStatus(),
                contract.getPickupDate(),
                contract.getDeliveryDate(),
                contract.getDriver().getName(),
                contract.getClient().getName(),
                contract.getFreight().getId(),
                contract.getVehicle().getId(),
                contract.getDriver().getId(),
                contract.getClient().getId());
    }

    public List<ContractResponseDTO> findAll() {
        return contractRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public ContractResponseDTO findById(Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found with id: " + id));
        return convertToDTO(contract);
    }

    public Contract create(ContractDTO contractDTO) {
        Contract contract = new Contract();
        contract.setStatus(Status.PENDING_CLIENT_APPROVAL);
        return saveContract(contract, contractDTO);
    }

    public Contract update(Long id, ContractDTO contractDTO) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found with id: " + id));
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

        Contract savedContract = contractRepository.save(contract);
        dto.setDisplay_name(savedContract.getDisplayName());
        return savedContract;
    }

    @Transactional
    public ContractResponseDTO approveContract(Long id) {
        Contract acceptedContract = contractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found with id: " + id));

        if (acceptedContract.getStatus() != Status.PENDING_CLIENT_APPROVAL) {
            throw new IllegalStateException("Only contracts with PENDING_CLIENT_APPROVAL status can be approved.");
        }

        Freight freight = acceptedContract.getFreight();

        // Rejeita todos os outros contratos pendentes para este frete
        List<Contract> pendingContracts = contractRepository.findByFreightAndStatus(freight,
                Status.PENDING_CLIENT_APPROVAL);
        for (Contract contract : pendingContracts) {
            if (!contract.getId().equals(id)) {
                contract.setStatus(Status.REJECTED);
                contractRepository.save(contract);
            }
        }

        // Aceita o contrato escolhido
        acceptedContract.setStatus(Status.ACTIVE);
        Contract savedContract = contractRepository.save(acceptedContract);

        // Fecha o frete para novas propostas
        freight.setStatus(FreightStatus.CLOSED);
        freightRepository.save(freight);

        return convertToDTO(savedContract);
    }

    @Transactional
    public ContractResponseDTO updateStatus(Long id, Status newStatus) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found with id: " + id));

        // Adicionar lógica de validação de transição de status se necessário
        // Ex: Não pode cancelar um contrato já concluído.

        contract.setStatus(newStatus);
        Contract savedContract = contractRepository.save(contract);

        // Se o contrato está começando, criar o registro de rastreamento
        if (newStatus == Status.IN_PROGRESS) {
            Freight freight = savedContract.getFreight();

            String originAddress = freight.getOrigin_city() + ", " + freight.getOrigin_state() + ", Brasil";
            String destinationAddress = freight.getDestination_city() + ", " + freight.getDestination_state()
                    + ", Brasil";

            String originCoordsStr = routeService.getCoordinates(originAddress);
            String destinationCoordsStr = routeService.getCoordinates(destinationAddress);

            String[] originCoords = originCoordsStr.split(",");
            String[] destinationCoords = destinationCoordsStr.split(",");

            double originLon = Double.parseDouble(originCoords[0]);
            double originLat = Double.parseDouble(originCoords[1]);
            double destLon = Double.parseDouble(destinationCoords[0]);
            double destLat = Double.parseDouble(destinationCoords[1]);

            Tracking tracking = trackingRepository.findByContract(savedContract).orElse(new Tracking());
            tracking.setContract(savedContract);
            tracking.setOriginLongitude(originLon);
            tracking.setOriginLatitude(originLat);
            tracking.setDestinationLongitude(destLon);
            tracking.setDestinationLatitude(destLat);
            tracking.setCurrentLongitude(originLon);
            tracking.setCurrentLatitude(originLat);

            trackingRepository.save(tracking);
        }

        return convertToDTO(savedContract);
    }

    public void delete(Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found with id: " + id));

        trackingRepository.findByContract(contract).ifPresent(trackingRepository::delete);

        contractRepository.delete(contract);
    }

    public List<ContractResponseDTO> findByClient(Long clientId) {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found with id: " + clientId));
        return contractRepository.findByClient(client).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<ContractResponseDTO> findByDriver(Long driverId) {
        User driver = userRepository.findById(driverId)
                .orElseThrow(() -> new EntityNotFoundException("Driver not found with id: " + driverId));
        return contractRepository.findByDriver(driver).stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<ContractResponseDTO> findByFreight(Long freightId) {
        Freight freight = freightRepository.findById(freightId)
                .orElseThrow(() -> new EntityNotFoundException("Freight not found with id: " + freightId));
        return contractRepository.findByFreight(freight).stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}