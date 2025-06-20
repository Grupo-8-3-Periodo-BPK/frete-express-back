package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.dto.TrackingDTO;
import br.com.express_frete.fretesexpress.model.*;
import br.com.express_frete.fretesexpress.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrackingServiceTest {

    @InjectMocks
    private TrackingService trackingService;

    @Mock
    private TrackingRepository trackingRepository;

    @Mock
    private FreightRepository freightRepository;

    @Mock
    private ContractRepository contractRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTrackingSuccess() {
        // Arrange
        TrackingDTO dto = new TrackingDTO();
        dto.setFreightId(1L);
        dto.setContractId(2L);
        dto.setCurrentLocation("São Paulo");
        dto.setContractUserId(3L);

        Freight freight = new Freight();
        freight.setId(1L);

        Contract contract = new Contract();
        contract.setId(2L);

        User user = new User();
        user.setId(3L);

        Tracking savedTracking = new Tracking();
        savedTracking.setId(10L);
        savedTracking.setFreight(freight);
        savedTracking.setContract(contract);
        savedTracking.setContractUser(user);
        savedTracking.setCurrentLocation("São Paulo");

        when(freightRepository.findById(1L)).thenReturn(Optional.of(freight));
        when(contractRepository.findById(2L)).thenReturn(Optional.of(contract));
        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        when(trackingRepository.save(any())).thenReturn(savedTracking);

        // Act
        Tracking result = trackingService.create(dto);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("São Paulo", result.getCurrentLocation());
    }

    @Test
    void testFindById_Success() {
        Tracking tracking = new Tracking();
        tracking.setId(1L);
        when(trackingRepository.findById(1L)).thenReturn(Optional.of(tracking));

        Tracking result = trackingService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testFindById_NotFound() {
        when(trackingRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> trackingService.findById(1L));
    }

    @Test
    void testDelete_Success() {
        Tracking tracking = new Tracking();
        tracking.setId(1L);
        when(trackingRepository.findById(1L)).thenReturn(Optional.of(tracking));

        trackingService.delete(1L);

        verify(trackingRepository).delete(tracking);
    }

    @Test
    void testFindAll() {
        List<Tracking> list = List.of(new Tracking(), new Tracking());
        when(trackingRepository.findAll()).thenReturn(list);

        List<Tracking> result = trackingService.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void testFindByFreight() {
        Freight freight = new Freight();
        freight.setId(1L);
        when(freightRepository.findById(1L)).thenReturn(Optional.of(freight));
        when(trackingRepository.findByFreight(freight)).thenReturn(List.of(new Tracking()));

        List<Tracking> result = trackingService.findByFreight(1L);
        assertEquals(1, result.size());
    }

    @Test
    void testFindByContract() {
        Contract contract = new Contract();
        contract.setId(1L);
        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        when(trackingRepository.findByContract(contract)).thenReturn(List.of(new Tracking()));

        List<Tracking> result = trackingService.findByContract(1L);
        assertEquals(1, result.size());
    }

    @Test
    void testGetLatestTrackingForContract() {
        Contract contract = new Contract();
        contract.setId(1L);
        Tracking latest = new Tracking();
        latest.setId(99L);

        when(contractRepository.findById(1L)).thenReturn(Optional.of(contract));
        when(trackingRepository.findTopByContractOrderByIdDesc(contract)).thenReturn(latest);

        Tracking result = trackingService.getLatestTrackingForContract(1L);

        assertEquals(99L, result.getId());
    }
}
