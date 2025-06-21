package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.dto.TrackingDTO;
import br.com.express_frete.fretesexpress.model.Tracking;
import br.com.express_frete.fretesexpress.service.TrackingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrackingControllerTest {

    @Mock
    private TrackingService trackingService;

    @InjectMocks
    private TrackingController trackingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTrackings() {
        Tracking tracking = new Tracking();
        when(trackingService.findAll()).thenReturn(List.of(tracking));

        ResponseEntity<List<Tracking>> response = trackingController.getAllTrackings();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetTrackingById() {
        Tracking tracking = new Tracking();
        when(trackingService.findById(1L)).thenReturn(tracking);

        ResponseEntity<Tracking> response = trackingController.getTrackingById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tracking, response.getBody());
    }

    @Test
    void testCreateTracking() {
        TrackingDTO dto = new TrackingDTO();
        Tracking saved = new Tracking();
        when(trackingService.create(dto)).thenReturn(saved);

        ResponseEntity<Tracking> response = trackingController.createTracking(dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(saved, response.getBody());
    }

    @Test
    void testUpdateTracking() {
        TrackingDTO dto = new TrackingDTO();
        Tracking updated = new Tracking();
        when(trackingService.update(1L, dto)).thenReturn(updated);

        ResponseEntity<Tracking> response = trackingController.updateTracking(1L, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updated, response.getBody());
    }

    @Test
    void testDeleteTracking() {
        doNothing().when(trackingService).delete(1L);

        ResponseEntity<Void> response = trackingController.deleteTracking(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(trackingService, times(1)).delete(1L);
    }

    @Test
    void testGetTrackingsByFreight() {
        Tracking tracking = new Tracking();
        when(trackingService.findByFreight(1L)).thenReturn(List.of(tracking));

        ResponseEntity<List<Tracking>> response = trackingController.getTrackingsByFreight(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetTrackingsByContract() {
        Tracking tracking = new Tracking();
        when(trackingService.findByContract(2L)).thenReturn(List.of(tracking));

        ResponseEntity<List<Tracking>> response = trackingController.getTrackingsByContract(2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testGetLatestTrackingForContract() {
        Tracking tracking = new Tracking();
        when(trackingService.getLatestTrackingForContract(2L)).thenReturn(tracking);

        ResponseEntity<Tracking> response = trackingController.getLatestTrackingForContract(2L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(tracking, response.getBody());
    }
}
