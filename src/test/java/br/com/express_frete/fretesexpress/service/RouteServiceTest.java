package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.dto.RouteResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RouteServiceTest {

  @Mock
  private RestTemplate restTemplate;

  private RouteService routeService;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    RestTemplateBuilder builder = new RestTemplateBuilder();
    routeService = new RouteService(builder, objectMapper);
    ReflectionTestUtils.setField(routeService, "restTemplate", restTemplate, RestTemplate.class);
    ReflectionTestUtils.setField(routeService, "apiKey", "test-api-key");
  }

  @Test
  void testGetDirections_Success() {
    String start = "start_coords";
    String end = "end_coords";
    String mockResponse = """
        {
            "features": [
                {
                    "geometry": {
                        "coordinates": [
                            [1.0, 2.0],
                            [3.0, 4.0]
                        ]
                    }
                }
            ]
        }
        """;
    ResponseEntity<String> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

    when(restTemplate.exchange(
        any(String.class),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))).thenReturn(responseEntity);

    RouteResponseDTO result = routeService.getDirections(start, end);

    assertNotNull(result);
    assertNotNull(result.getCoordinates());
    assertEquals(2, result.getCoordinates().size());
    assertEquals(List.of(2.0, 1.0), result.getCoordinates().get(0));
    assertEquals(List.of(4.0, 3.0), result.getCoordinates().get(1));
  }

  @Test
  void testGetCoordinates_Success() {
    String address = "some address";
    String mockResponse = """
        {
            "features": [
                {
                    "geometry": {
                        "coordinates": [10.0, 20.0]
                    }
                }
            ]
        }
        """;
    ResponseEntity<String> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

    when(restTemplate.exchange(
        any(String.class),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))).thenReturn(responseEntity);

    String coordinates = routeService.getCoordinates(address);

    assertEquals("10.0,20.0", coordinates);
  }

  @Test
  void testGetDirections_ApiError() {
    String start = "start_coords";
    String end = "end_coords";

    when(restTemplate.exchange(
        any(String.class),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))).thenThrow(new RuntimeException("API Error"));

    Exception exception = assertThrows(RuntimeException.class, () -> {
      routeService.getDirections(start, end);
    });

    assertTrue(exception.getMessage().contains("O serviço de rotas externo está indisponível no momento."));
  }

  @Test
  void testGetCoordinates_ApiError() {
    String address = "some address";

    when(restTemplate.exchange(
        any(String.class),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))).thenThrow(new RuntimeException("API Error"));

    Exception exception = assertThrows(RuntimeException.class, () -> {
      routeService.getCoordinates(address);
    });

    assertTrue(exception.getMessage().contains("Falha ao buscar coordenadas."));
  }

  @Test
  void testGetCoordinates_NoFeatures() {
    String address = "unknown address";
    String mockResponse = """
        { "features": [] }
        """;
    ResponseEntity<String> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

    when(restTemplate.exchange(
        any(String.class),
        eq(HttpMethod.GET),
        any(HttpEntity.class),
        eq(String.class))).thenReturn(responseEntity);

    Exception exception = assertThrows(RuntimeException.class, () -> {
      routeService.getCoordinates(address);
    });

    assertTrue(exception.getMessage().contains("Não foi possível encontrar coordenadas para o endereço"));
  }
}
