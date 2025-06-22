package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.dto.TrackingDTO;
import br.com.express_frete.fretesexpress.model.Contract;
import br.com.express_frete.fretesexpress.model.Tracking;
import br.com.express_frete.fretesexpress.repository.ContractRepository;
import br.com.express_frete.fretesexpress.repository.TrackingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrackingService {

    @Autowired
    private TrackingRepository trackingRepository;

    @Autowired
    private ContractRepository contractRepository;

    public List<Tracking> findAll() {
        return trackingRepository.findAll();
    }

    public Tracking findById(Long id) {
        return trackingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tracking not found with id: " + id));
    }

    public Tracking createOrUpdate(TrackingDTO trackingDTO) {
        Contract contract = contractRepository.findById(trackingDTO.getContractId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Contract not found with id: " + trackingDTO.getContractId()));

        Optional<Tracking> existingTrackingOpt = trackingRepository.findByContract(contract);

        Tracking tracking;
        if (existingTrackingOpt.isPresent()) {
            // Atualiza o rastreamento existente
            tracking = existingTrackingOpt.get();
            tracking.setCurrentLatitude(trackingDTO.getCurrentLatitude());
            tracking.setCurrentLongitude(trackingDTO.getCurrentLongitude());
        } else {
            // Cria um novo rastreamento
            if (trackingDTO.getOriginLatitude() == null || trackingDTO.getOriginLongitude() == null ||
                    trackingDTO.getDestinationLatitude() == null || trackingDTO.getDestinationLongitude() == null) {
                throw new IllegalArgumentException(
                        "Origin and destination coordinates are required for the first tracking entry.");
            }
            tracking = new Tracking();
            tracking.setContract(contract);
            tracking.setCurrentLatitude(trackingDTO.getCurrentLatitude());
            tracking.setCurrentLongitude(trackingDTO.getCurrentLongitude());
            tracking.setOriginLatitude(trackingDTO.getOriginLatitude());
            tracking.setOriginLongitude(trackingDTO.getOriginLongitude());
            tracking.setDestinationLatitude(trackingDTO.getDestinationLatitude());
            tracking.setDestinationLongitude(trackingDTO.getDestinationLongitude());
        }

        return trackingRepository.save(tracking);
    }

    public void delete(Long id) {
        Tracking tracking = findById(id);
        trackingRepository.delete(tracking);
    }

    public Tracking findByContract(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found with id: " + contractId));
        return trackingRepository.findByContract(contract)
                .orElse(null);
    }

    public Tracking getLatestTrackingForContract(Long contractId) {
        return findByContract(contractId);
    }
}