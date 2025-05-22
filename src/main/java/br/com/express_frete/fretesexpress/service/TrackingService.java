package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.dto.TrackingDTO;
import br.com.express_frete.fretesexpress.model.Contract;
import br.com.express_frete.fretesexpress.model.Freight;
import br.com.express_frete.fretesexpress.model.Tracking;
import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.repository.ContractRepository;
import br.com.express_frete.fretesexpress.repository.FreightRepository;
import br.com.express_frete.fretesexpress.repository.TrackingRepository;
import br.com.express_frete.fretesexpress.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TrackingService {

    @Autowired
    private TrackingRepository trackingRepository;

    @Autowired
    private FreightRepository freightRepository;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Tracking> findAll() {
        return trackingRepository.findAll();
    }

    public Tracking findById(Long id) {
        return trackingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tracking not found with id: " + id));
    }

    public Tracking create(TrackingDTO trackingDTO) {
        Tracking tracking = new Tracking();

        // Se a data de atualização não for fornecida, use a data atual
        if (trackingDTO.getUpdateDate() == null) {
            tracking.setUpdateDate(LocalDate.now());
        } else {
            tracking.setUpdateDate(trackingDTO.getUpdateDate());
        }

        return saveTracking(tracking, trackingDTO);
    }

    public Tracking update(Long id, TrackingDTO trackingDTO) {
        Tracking tracking = findById(id);
        return saveTracking(tracking, trackingDTO);
    }

    private Tracking saveTracking(Tracking tracking, TrackingDTO dto) {
        Freight freight = freightRepository.findById(dto.getFreightId())
                .orElseThrow(() -> new EntityNotFoundException("Freight not found with id: " + dto.getFreightId()));
        tracking.setFreight(freight);

        Contract contract = contractRepository.findById(dto.getContractId())
                .orElseThrow(() -> new EntityNotFoundException("Contract not found with id: " + dto.getContractId()));
        tracking.setContract(contract);

        tracking.setStatus(dto.getStatus());
        tracking.setCurrentLocation(dto.getCurrentLocation());

        if (dto.getUpdateDate() != null) {
            tracking.setUpdateDate(dto.getUpdateDate());
        }

        if (dto.getContractUserId() != null) {
            User contractUser = userRepository.findById(dto.getContractUserId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("User not found with id: " + dto.getContractUserId()));
            tracking.setContractUser(contractUser);
        }

        return trackingRepository.save(tracking);
    }

    public void delete(Long id) {
        Tracking tracking = findById(id);
        trackingRepository.delete(tracking);
    }

    public List<Tracking> findByFreight(Long freightId) {
        Freight freight = freightRepository.findById(freightId)
                .orElseThrow(() -> new EntityNotFoundException("Freight not found with id: " + freightId));
        return trackingRepository.findByFreight(freight);
    }

    public List<Tracking> findByContract(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found with id: " + contractId));
        return trackingRepository.findByContract(contract);
    }

    public Tracking getLatestTrackingForContract(Long contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new EntityNotFoundException("Contract not found with id: " + contractId));
        return trackingRepository.findTopByContractOrderByUpdateDateDesc(contract);
    }
}