package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.model.RecoveryLog;
import br.com.express_frete.fretesexpress.repository.RecoveryLogRepository;
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

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para RecoveryLogController")
class RecoveryLogControllerTest {

    @Mock
    private RecoveryLogRepository logRepository;

    @InjectMocks
    private RecoveryLogController recoveryLogController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private RecoveryLog recoveryLog1;
    private RecoveryLog recoveryLog2;
    private RecoveryLog recoveryLog3;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(recoveryLogController)
                .build();

        objectMapper = new ObjectMapper();

        // Criar dados de teste
        recoveryLog1 = createRecoveryLog(1L, "user1@test.com", "SOLICITADO", "192.168.1.1");
        recoveryLog2 = createRecoveryLog(2L, "user2@test.com", "REDEFINIDO", "192.168.1.2");
        recoveryLog3 = createRecoveryLog(3L, "user1@test.com", "FALHA", "192.168.1.3");
    }

    private RecoveryLog createRecoveryLog(Long id, String email, String status, String ip) {
        RecoveryLog log = new RecoveryLog();
        log.setEmail(email);
        log.setStatus(status);
        log.setIp(ip);
        log.setTimestamp(Instant.now());
        return log;
    }

    @Test
    @DisplayName("Deve retornar todos os logs quando GET /api/recovery/logs")
    void getAllLogs_DeveRetornarTodosOsLogs() throws Exception {
        // Given
        List<RecoveryLog> logs = Arrays.asList(recoveryLog1, recoveryLog2, recoveryLog3);
        when(logRepository.findAll()).thenReturn(logs);

        // When & Then
        mockMvc.perform(get("/api/recovery/logs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].email", is("user1@test.com")))
                .andExpect(jsonPath("$[0].status", is("SOLICITADO")))
                .andExpect(jsonPath("$[0].ip", is("192.168.1.1")))
                .andExpect(jsonPath("$[1].email", is("user2@test.com")))
                .andExpect(jsonPath("$[1].status", is("REDEFINIDO")))
                .andExpect(jsonPath("$[1].ip", is("192.168.1.2")))
                .andExpect(jsonPath("$[2].email", is("user1@test.com")))
                .andExpect(jsonPath("$[2].status", is("FALHA")))
                .andExpect(jsonPath("$[2].ip", is("192.168.1.3")));

        verify(logRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não há logs")
    void getAllLogs_DeveRetornarListaVaziaQuandoNaoHaLogs() throws Exception {
        // Given
        when(logRepository.findAll()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/recovery/logs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(logRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve retornar logs por email quando GET /api/recovery/logs/{email}")
    void getLogsByEmail_DeveRetornarLogsPorEmail() throws Exception {
        // Given
        String email = "user1@test.com";
        List<RecoveryLog> logsDoUsuario = Arrays.asList(recoveryLog1, recoveryLog3);
        when(logRepository.findByEmail(email)).thenReturn(logsDoUsuario);

        // When & Then
        mockMvc.perform(get("/api/recovery/logs/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email", is("user1@test.com")))
                .andExpect(jsonPath("$[0].status", is("SOLICITADO")))
                .andExpect(jsonPath("$[1].email", is("user1@test.com")))
                .andExpect(jsonPath("$[1].status", is("FALHA")));

        verify(logRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando email não possui logs")
    void getLogsByEmail_DeveRetornarListaVaziaQuandoEmailNaoPossuiLogs() throws Exception {
        // Given
        String email = "semlog@test.com";
        when(logRepository.findByEmail(email)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/recovery/logs/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(logRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Deve tratar email com caracteres especiais")
    void getLogsByEmail_DeveTratarEmailComCaracteresEspeciais() throws Exception {
        // Given
        String email = "user+test@example.com";
        List<RecoveryLog> logs = Arrays.asList(recoveryLog1);
        when(logRepository.findByEmail(email)).thenReturn(logs);

        // When & Then
        mockMvc.perform(get("/api/recovery/logs/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(logRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Deve verificar se repository é chamado corretamente para todos os logs")
    void getAllLogs_DeveVerificarChamadaRepository() throws Exception {
        // Given
        when(logRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        mockMvc.perform(get("/api/recovery/logs"));

        // Then
        verify(logRepository, times(1)).findAll();
        verifyNoMoreInteractions(logRepository);
    }

    @Test
    @DisplayName("Deve verificar se repository é chamado corretamente para logs por email")
    void getLogsByEmail_DeveVerificarChamadaRepository() throws Exception {
        // Given
        String email = "test@example.com";
        when(logRepository.findByEmail(anyString())).thenReturn(Collections.emptyList());

        // When
        mockMvc.perform(get("/api/recovery/logs/{email}", email));

        // Then
        verify(logRepository, times(1)).findByEmail(email);
        verifyNoMoreInteractions(logRepository);
    }

    @Test
    @DisplayName("Deve retornar logs com diferentes status")
    void getLogsByEmail_DeveRetornarLogsComDiferentesStatus() throws Exception {
        // Given
        String email = "multilog@test.com";
        RecoveryLog logSolicitado = createRecoveryLog(4L, email, "SOLICITADO", "192.168.1.4");
        RecoveryLog logRedefinido = createRecoveryLog(5L, email, "REDEFINIDO", "192.168.1.5");
        RecoveryLog logFalha = createRecoveryLog(6L, email, "FALHA", "192.168.1.6");

        List<RecoveryLog> logs = Arrays.asList(logSolicitado, logRedefinido, logFalha);
        when(logRepository.findByEmail(email)).thenReturn(logs);

        // When & Then
        mockMvc.perform(get("/api/recovery/logs/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].status", is("SOLICITADO")))
                .andExpect(jsonPath("$[1].status", is("REDEFINIDO")))
                .andExpect(jsonPath("$[2].status", is("FALHA")));

        verify(logRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("Deve verificar estrutura da resposta JSON")
    void getAllLogs_DeveVerificarEstruturaRespostaJSON() throws Exception {
        // Given
        List<RecoveryLog> logs = Arrays.asList(recoveryLog1);
        when(logRepository.findAll()).thenReturn(logs);

        // When & Then
        mockMvc.perform(get("/api/recovery/logs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].email").exists())
                .andExpect(jsonPath("$[0].status").exists())
                .andExpect(jsonPath("$[0].ip").exists())
                .andExpect(jsonPath("$[0].timestamp").exists());

        verify(logRepository, times(1)).findAll();
    }
}