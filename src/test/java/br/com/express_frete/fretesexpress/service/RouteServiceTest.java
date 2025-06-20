package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.dto.CoordinatesResponseDTO;
import br.com.express_frete.fretesexpress.dto.DirectionsResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class RouteServiceTest {

    private RestTemplate restTemplate;
    private RouteService routeService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        RestTemplateBuilder builder = mock(RestTemplateBuilder.class);
        when(builder.build()).thenReturn(restTemplate);
        routeService = new RouteService(builder);
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetDirectionsSuccess() throws Exception {
        // JSONs fictícios simplificados
        String jsonCoordinates = """
            {
              "features": [
                {
                  "geometry": {
                    "coordinates": [1.0, 2.0]
                  }
                }
              ]
            }
            """;

        String jsonDirections = """
            {
              "features": [
                {
                  "properties": {
                    "segments": [
                      {
                        "distance": 5000.0
                      }
                    ]
                  }
                }
              ]
            }
            """;

        when(restTemplate.getForObject(contains("geocode"), eq(String.class)))
                .thenReturn(jsonCoordinates);
        when(restTemplate.getForObject(contains("directions"), eq(String.class)))
                .thenReturn(jsonDirections);

        String result = routeService.getDirections("São Paulo", "Campinas");
        assertEquals("Distance: 5.00KM", result);

    }
}
