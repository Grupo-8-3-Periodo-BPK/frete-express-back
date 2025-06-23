package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.dto.TrackingDTO;
import br.com.express_frete.fretesexpress.model.Contract;
import br.com.express_frete.fretesexpress.model.Tracking;
import br.com.express_frete.fretesexpress.repository.ContractRepository;
import br.com.express_frete.fretesexpress.repository.TrackingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackingServiceTest {

    @InjectMocks
    private TrackingService trackingService;

    @Mock
    private TrackingRepository trackingRepository;

    @Mock
    private ContractRepository contractRepository;

    private Contract contract;
    private Tracking tracking;
    private TrackingDTO trackingDTO;

    @BeforeEach
    void setUp() {
        contract = new Contract();
        contract.setId(1L);

        tracking = new Tracking();
        tracking.setId(10L);
        tracking.setContract(contract);
        tracking.setCurrentLatitude(10.0);
        tracking.setCurrentLongitude(20.0);

        trackingDTO = new TrackingDTO();
        trackingDTO.setContractId(1L);
        trackingDTO.setCurrentLatitude(15.0);
        trackingDTO.setCurrentLongitude(25.0);
    }

    @Test
    void testCreateOrUpdate_CreateNew() {
        trackingDTO.setOriginLatitude(1.0);
        trackingDTO.setOriginLongitude(2.0);
        trackingDTO.setDestinationLatitude(3.0);
        trackingDTO.setDestinationLongitude(4.0);

        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        when(trackingRepository.findByContract(contract)).thenReturn(Optional.empty());
        when(trackingRepository.save(any(Tracking.class))).thenAnswer(i -> i.getArgument(0));

        Tracking result = trackingService.createOrUpdate(trackingDTO);

        assertNotNull(result);
        assertEquals(15.0, result.getCurrentLatitude());
        assertEquals(1.0, result.getOriginLatitude());
        verify(trackingRepository).save(any(Tracking.class));
    }

    @Test
    void testCreateOrUpdate_UpdateExisting() {
        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        when(trackingRepository.findByContract(contract)).thenReturn(Optional.of(tracking));
        when(trackingRepository.save(any(Tracking.class))).thenAnswer(i -> i.getArgument(0));

        Tracking result = trackingService.createOrUpdate(trackingDTO);

        assertNotNull(result);
        assertEquals(15.0, result.getCurrentLatitude());
        assertEquals(10L, result.getId());
        verify(trackingRepository).save(tracking);
    }

    @Test
    void testCreateOrUpdate_CreateNew_MissingCoords_ThrowsException() {
        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        when(trackingRepository.findByContract(contract)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> trackingService.createOrUpdate(trackingDTO));
    }

    @Test
    void testFindById_Success() {
        when(trackingRepository.findById(1L)).thenReturn(Optional.of(tracking));
        Tracking result = trackingService.findById(1L);
        assertNotNull(result);
        assertEquals(10L, result.getId());
    }

    @Test
    void testFindById_NotFound() {
        when(trackingRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> trackingService.findById(1L));
    }

    @Test
    void testDelete_Success() {
        when(trackingRepository.findById(1L)).thenReturn(Optional.of(tracking));
        doNothing().when(trackingRepository).delete(tracking);
        trackingService.delete(1L);
        verify(trackingRepository).delete(tracking);
    }

    @Test
    void testFindAll() {
        when(trackingRepository.findAll()).thenReturn(List.of(tracking, new Tracking()));
        List<Tracking> result = trackingService.findAll();
        assertEquals(2, result.size());
    }

    @Test
    void testFindByContract_Success() {
        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        when(trackingRepository.findByContract(contract)).thenReturn(Optional.of(tracking));

        Tracking result = trackingService.findByContract(1L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
    }

    @Test
    void testFindByContract_NotFound() {
        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        when(trackingRepository.findByContract(contract)).thenReturn(Optional.empty());

        Tracking result = trackingService.findByContract(1L);

        assertNull(result);
    }

    @Test
    void testGetLatestTrackingForContract() {
        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        when(trackingRepository.findByContract(contract)).thenReturn(Optional.of(tracking));

        Tracking result = trackingService.getLatestTrackingForContract(1L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
    }
}
