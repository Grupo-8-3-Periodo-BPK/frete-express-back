package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.dto.TrackingDTO;
import br.com.express_frete.fretesexpress.model.Tracking;
import br.com.express_frete.fretesexpress.service.TrackingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TrackingControllerTest {

    @Mock
    private TrackingService trackingService;

    @InjectMocks
    private TrackingController trackingController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(trackingController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetAllTrackings() throws Exception {
        when(trackingService.findAll()).thenReturn(List.of(new Tracking()));

        mockMvc.perform(get("/api/tracking"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void testGetTrackingById() throws Exception {
        Tracking tracking = new Tracking();
        tracking.setId(1L);
        when(trackingService.findById(1L)).thenReturn(tracking);

        mockMvc.perform(get("/api/tracking/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testCreateTracking() throws Exception {
        TrackingDTO dto = new TrackingDTO();
        dto.setContractId(1L);
        dto.setCurrentLatitude(10.0);
        dto.setCurrentLongitude(20.0);

        Tracking saved = new Tracking();
        saved.setId(1L);

        when(trackingService.createOrUpdate(any(TrackingDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/tracking")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testDeleteTracking() throws Exception {
        doNothing().when(trackingService).delete(1L);

        mockMvc.perform(delete("/api/tracking/1"))
                .andExpect(status().isNoContent());

        verify(trackingService).delete(1L);
    }

    @Test
    void testGetTrackingsByContract() throws Exception {
        Tracking tracking = new Tracking();
        tracking.setId(1L);
        when(trackingService.findByContract(1L)).thenReturn(tracking);

        mockMvc.perform(get("/api/tracking/contract/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testGetLatestTrackingForContract() throws Exception {
        Tracking tracking = new Tracking();
        tracking.setId(1L);
        when(trackingService.getLatestTrackingForContract(1L)).thenReturn(tracking);

        mockMvc.perform(get("/api/tracking/contract/1/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}
