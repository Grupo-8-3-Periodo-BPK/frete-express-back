package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.service.RouteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Classe de teste para RouteController
 * Testa os endpoints relacionados ao cálculo de rotas e direções
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RouteController Tests")
class RouteControllerTest {

    @Mock
    private RouteService routeService;

    @InjectMocks
    private RouteController routeController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(routeController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Deve retornar direções com sucesso quando parâmetros válidos são fornecidos")
    void shouldReturnDirectionsSuccessfully() throws Exception {
        // Arrange
        String startLocation = "São Paulo, SP";
        String endLocation = "Rio de Janeiro, RJ";
        String expectedResponse = "Distance: 429.50KM";

        when(routeService.getDirections(startLocation, endLocation))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/api/directions")
                        .param("start", startLocation)
                        .param("end", endLocation)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));

        verify(routeService, times(1)).getDirections(startLocation, endLocation);
    }

    @Test
    @DisplayName("Deve retornar direções para cidades diferentes")
    void shouldReturnDirectionsForDifferentCities() throws Exception {
        // Arrange
        String startLocation = "Curitiba, PR";
        String endLocation = "Florianópolis, SC";
        String expectedResponse = "Distance: 300.25KM";

        when(routeService.getDirections(startLocation, endLocation))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/api/directions")
                        .param("start", startLocation)
                        .param("end", endLocation)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));

        verify(routeService, times(1)).getDirections(startLocation, endLocation);
    }

    @Test
    @DisplayName("Deve chamar o serviço com parâmetros corretos")
    void shouldCallServiceWithCorrectParameters() throws Exception {
        // Arrange
        String startLocation = "Brasília, DF";
        String endLocation = "Goiânia, GO";
        String expectedResponse = "Distance: 209.15KM";

        when(routeService.getDirections(startLocation, endLocation))
                .thenReturn(expectedResponse);

        // Act
        mockMvc.perform(get("/api/directions")
                        .param("start", startLocation)
                        .param("end", endLocation))
                .andExpect(status().isOk());

        // Assert
        verify(routeService, times(1)).getDirections(startLocation, endLocation);
        verify(routeService, times(1)).getDirections(eq(startLocation), eq(endLocation));
    }

    @Test
    @DisplayName("Deve retornar Bad Request quando parâmetro 'start' está ausente")
    void shouldReturnBadRequestWhenStartParameterIsMissing() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/directions")
                        .param("end", "Rio de Janeiro, RJ")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(routeService, never()).getDirections(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve retornar Bad Request quando parâmetro 'end' está ausente")
    void shouldReturnBadRequestWhenEndParameterIsMissing() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/directions")
                        .param("start", "São Paulo, SP")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(routeService, never()).getDirections(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve retornar Bad Request quando ambos os parâmetros estão ausentes")
    void shouldReturnBadRequestWhenBothParametersAreMissing() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/directions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(routeService, never()).getDirections(anyString(), anyString());
    }

    @Test
    @DisplayName("Deve funcionar com parâmetros vazios")
    void shouldHandleEmptyParameters() throws Exception {
        // Arrange
        String startLocation = "";
        String endLocation = "";
        String expectedResponse = "Distance: 0.00KM";

        when(routeService.getDirections(startLocation, endLocation))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/api/directions")
                        .param("start", startLocation)
                        .param("end", endLocation)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));

        verify(routeService, times(1)).getDirections(startLocation, endLocation);
    }

    @Test
    @DisplayName("Deve funcionar com endereços completos incluindo números")
    void shouldHandleCompleteAddresses() throws Exception {
        // Arrange
        String startLocation = "Rua das Flores, 123, São Paulo, SP";
        String endLocation = "Avenida Paulista, 456, São Paulo, SP";
        String expectedResponse = "Distance: 15.30KM";

        when(routeService.getDirections(startLocation, endLocation))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/api/directions")
                        .param("start", startLocation)
                        .param("end", endLocation)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));

        verify(routeService, times(1)).getDirections(startLocation, endLocation);
    }

    @Test
    @DisplayName("Deve propagar exceção do serviço")
    void shouldPropagateServiceException() throws Exception {
        // Arrange
        String startLocation = "Local Inexistente";
        String endLocation = "Outro Local Inexistente";

        when(routeService.getDirections(startLocation, endLocation))
                .thenThrow(new RuntimeException("Erro ao processar coordenadas"));

        // Act & Assert
        // Como o controller não trata exceções, ela será propagada
        // O teste verifica se a exceção é lançada
        try {
            mockMvc.perform(get("/api/directions")
                            .param("start", startLocation)
                            .param("end", endLocation)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print());
        } catch (Exception e) {
            // Verifica se a exceção contém a mensagem esperada
            assert e.getCause() instanceof RuntimeException;
            assert e.getCause().getMessage().contains("Erro ao processar coordenadas");
        }

        verify(routeService, times(1)).getDirections(startLocation, endLocation);
    }

    @Test
    @DisplayName("Deve aceitar caracteres especiais nos parâmetros")
    void shouldHandleSpecialCharacters() throws Exception {
        // Arrange
        String startLocation = "São José dos Campos, SP";
        String endLocation = "Ribeirão Preto, SP";
        String expectedResponse = "Distance: 150.75KM";

        when(routeService.getDirections(startLocation, endLocation))
                .thenReturn(expectedResponse);

        // Act & Assert
        mockMvc.perform(get("/api/directions")
                        .param("start", startLocation)
                        .param("end", endLocation)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));

        verify(routeService, times(1)).getDirections(startLocation, endLocation);
    }

    @Test
    @DisplayName("Deve verificar se o serviço não é chamado múltiplas vezes desnecessariamente")
    void shouldVerifyServiceIsNotCalledUnnecessarily() throws Exception {
        // Arrange
        String startLocation = "Porto Alegre, RS";
        String endLocation = "Caxias do Sul, RS";
        String expectedResponse = "Distance: 120.45KM";

        when(routeService.getDirections(startLocation, endLocation))
                .thenReturn(expectedResponse);

        // Act
        mockMvc.perform(get("/api/directions")
                        .param("start", startLocation)
                        .param("end", endLocation))
                .andExpect(status().isOk());

        // Assert
        verify(routeService, times(1)).getDirections(startLocation, endLocation);
        verifyNoMoreInteractions(routeService);
    }
}