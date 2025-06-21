package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.dto.NewPasswordDTO;
import br.com.express_frete.fretesexpress.dto.RecoveryRequestDTO;
import br.com.express_frete.fretesexpress.dto.TokenValidationDTO;
import br.com.express_frete.fretesexpress.service.PasswordRecoveryService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PasswordRecoveryControllerTest {

    @Mock
    private PasswordRecoveryService recoveryService;

    @InjectMocks
    private PasswordRecoveryController passwordRecoveryController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(passwordRecoveryController).build();
        objectMapper = new ObjectMapper();
    }

    // ===== TESTES PARA /api/recovery/request =====

    @Test
    void requestRecovery_ComEmailValido_DeveRetornarSucesso() throws Exception {
        // Arrange
        RecoveryRequestDTO dto = new RecoveryRequestDTO();
        dto.setEmailOrPhone("usuario@teste.com");

        doNothing().when(recoveryService).requestRecovery(any(RecoveryRequestDTO.class));

        // Act & Assert
        mockMvc.perform(post("/api/recovery/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Token de recuperação enviado com sucesso."));

        verify(recoveryService, times(1)).requestRecovery(any(RecoveryRequestDTO.class));
    }

    @Test
    void requestRecovery_ComEmailInvalido_DeveRetornarBadRequest() throws Exception {
        // Arrange
        RecoveryRequestDTO dto = new RecoveryRequestDTO();
        dto.setEmailOrPhone("email@inexistente.com");

        doThrow(new IllegalArgumentException("Usuário não encontrado com o e-mail informado"))
                .when(recoveryService).requestRecovery(any(RecoveryRequestDTO.class));

        // Act & Assert
        mockMvc.perform(post("/api/recovery/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Usuário não encontrado com o e-mail informado"));

        verify(recoveryService, times(1)).requestRecovery(any(RecoveryRequestDTO.class));
    }

    @Test
    void requestRecovery_ComErroInterno_DeveRetornarInternalServerError() throws Exception {
        // Arrange
        RecoveryRequestDTO dto = new RecoveryRequestDTO();
        dto.setEmailOrPhone("usuario@teste.com");

        doThrow(new RuntimeException("Erro de conexão com banco"))
                .when(recoveryService).requestRecovery(any(RecoveryRequestDTO.class));

        // Act & Assert
        mockMvc.perform(post("/api/recovery/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Erro ao processar recuperação: Erro de conexão com banco"));

        verify(recoveryService, times(1)).requestRecovery(any(RecoveryRequestDTO.class));
    }

    @Test
    void requestRecovery_ComDadosVazios_DeveRetornarBadRequest() throws Exception {
        // Arrange
        RecoveryRequestDTO dto = new RecoveryRequestDTO();
        // emailOrPhone vazio

        // Act & Assert
        mockMvc.perform(post("/api/recovery/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(recoveryService, never()).requestRecovery(any(RecoveryRequestDTO.class));
    }

    // ===== TESTES PARA /api/recovery/validate =====

    @Test
    void validateToken_ComTokenValido_DeveRetornarSucesso() throws Exception {
        // Arrange
        TokenValidationDTO dto = new TokenValidationDTO();
        dto.setToken("token-valido-123");

        when(recoveryService.validateToken(any(TokenValidationDTO.class)))
                .thenReturn(true);

        // Act & Assert
        mockMvc.perform(post("/api/recovery/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Token válido"));

        verify(recoveryService, times(1)).validateToken(any(TokenValidationDTO.class));
    }

    @Test
    void validateToken_ComTokenInvalido_DeveRetornarBadRequest() throws Exception {
        // Arrange
        TokenValidationDTO dto = new TokenValidationDTO();
        dto.setToken("token-invalido-123");

        when(recoveryService.validateToken(any(TokenValidationDTO.class)))
                .thenReturn(false);

        // Act & Assert
        mockMvc.perform(post("/api/recovery/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Token inválido ou expirado"));

        verify(recoveryService, times(1)).validateToken(any(TokenValidationDTO.class));
    }

    @Test
    void validateToken_ComTokenVazio_DeveRetornarBadRequest() throws Exception {
        // Arrange
        TokenValidationDTO dto = new TokenValidationDTO();
        // token vazio

        // Act & Assert
        mockMvc.perform(post("/api/recovery/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(recoveryService, never()).validateToken(any(TokenValidationDTO.class));
    }

    // ===== TESTES PARA /api/recovery/reset =====

    @Test
    void resetPassword_ComDadosValidos_DeveRetornarSucesso() throws Exception {
        // Arrange
        NewPasswordDTO dto = new NewPasswordDTO();
        dto.setToken("token-valido-123");
        dto.setNewPassword("NovaSenha123");

        doNothing().when(recoveryService).resetPassword(any(NewPasswordDTO.class));

        // Act & Assert
        mockMvc.perform(post("/api/recovery/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().string("Senha redefinida com sucesso."));

        verify(recoveryService, times(1)).resetPassword(any(NewPasswordDTO.class));
    }

    @Test
    void resetPassword_ComTokenInvalido_DeveRetornarBadRequest() throws Exception {
        // Arrange
        NewPasswordDTO dto = new NewPasswordDTO();
        dto.setToken("token-invalido");
        dto.setNewPassword("NovaSenha123");

        doThrow(new IllegalArgumentException("Token inválido"))
                .when(recoveryService).resetPassword(any(NewPasswordDTO.class));

        // Act & Assert
        mockMvc.perform(post("/api/recovery/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Token inválido"));

        verify(recoveryService, times(1)).resetPassword(any(NewPasswordDTO.class));
    }

    @Test
    void resetPassword_ComTokenExpirado_DeveRetornarBadRequest() throws Exception {
        // Arrange
        NewPasswordDTO dto = new NewPasswordDTO();
        dto.setToken("token-expirado");
        dto.setNewPassword("NovaSenha123");

        doThrow(new IllegalArgumentException("Token expirado ou já utilizado"))
                .when(recoveryService).resetPassword(any(NewPasswordDTO.class));

        // Act & Assert
        mockMvc.perform(post("/api/recovery/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Token expirado ou já utilizado"));

        verify(recoveryService, times(1)).resetPassword(any(NewPasswordDTO.class));
    }

    @Test
    void resetPassword_ComErroInterno_DeveRetornarInternalServerError() throws Exception {
        // Arrange
        NewPasswordDTO dto = new NewPasswordDTO();
        dto.setToken("token-valido");
        dto.setNewPassword("NovaSenha123");

        doThrow(new RuntimeException("Erro de conexão com banco"))
                .when(recoveryService).resetPassword(any(NewPasswordDTO.class));

        // Act & Assert
        mockMvc.perform(post("/api/recovery/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Erro ao redefinir senha: Erro de conexão com banco"));

        verify(recoveryService, times(1)).resetPassword(any(NewPasswordDTO.class));
    }

    @Test
    void resetPassword_ComDadosVazios_DeveRetornarBadRequest() throws Exception {
        // Arrange
        NewPasswordDTO dto = new NewPasswordDTO();
        // campos vazios

        // Act & Assert
        mockMvc.perform(post("/api/recovery/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(recoveryService, never()).resetPassword(any(NewPasswordDTO.class));
    }

    @Test
    void resetPassword_ComSenhaCurta_DeveRetornarBadRequest() throws Exception {
        // Arrange
        NewPasswordDTO dto = new NewPasswordDTO();
        dto.setToken("token-valido");
        dto.setNewPassword("123"); // menos de 8 caracteres

        // Act & Assert
        mockMvc.perform(post("/api/recovery/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verify(recoveryService, never()).resetPassword(any(NewPasswordDTO.class));
    }

    // ===== TESTES DE INTEGRAÇÃO =====

    @Test
    void fluxoCompleto_RecuperacaoSenha_DeveProcessarCorretamente() throws Exception {
        // Arrange
        String email = "usuario@teste.com";
        String token = "token-gerado-123";
        String novaSenha = "NovaSenha123";

        // 1. Solicitar recuperação
        RecoveryRequestDTO requestDto = new RecoveryRequestDTO();
        requestDto.setEmailOrPhone(email);
        doNothing().when(recoveryService).requestRecovery(any(RecoveryRequestDTO.class));

        // 2. Validar token
        TokenValidationDTO validationDto = new TokenValidationDTO();
        validationDto.setToken(token);
        when(recoveryService.validateToken(any(TokenValidationDTO.class))).thenReturn(true);

        // 3. Redefinir senha
        NewPasswordDTO resetDto = new NewPasswordDTO();
        resetDto.setToken(token);
        resetDto.setNewPassword(novaSenha);
        doNothing().when(recoveryService).resetPassword(any(NewPasswordDTO.class));

        // Act & Assert - Fluxo completo

        // 1. Solicitar recuperação
        mockMvc.perform(post("/api/recovery/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        // 2. Validar token
        mockMvc.perform(post("/api/recovery/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validationDto)))
                .andExpect(status().isOk());

        // 3. Redefinir senha
        mockMvc.perform(post("/api/recovery/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetDto)))
                .andExpect(status().isOk());

        // Verificar que todos os métodos foram chamados
        verify(recoveryService, times(1)).requestRecovery(any(RecoveryRequestDTO.class));
        verify(recoveryService, times(1)).validateToken(any(TokenValidationDTO.class));
        verify(recoveryService, times(1)).resetPassword(any(NewPasswordDTO.class));
    }
}