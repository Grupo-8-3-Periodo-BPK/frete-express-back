package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.service.EmailService;
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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmailControllerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmailController emailController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(emailController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testSendEmail_Success() throws Exception {
        // Arrange
        EmailController.EmailRequest emailRequest = new EmailController.EmailRequest();
        emailRequest.setTo("teste@exemplo.com");
        emailRequest.setSubject("Assunto de Teste");
        emailRequest.setContent("Conteúdo do email de teste");

        String requestJson = objectMapper.writeValueAsString(emailRequest);

        // Não precisa configurar comportamento para void method quando não há exceção


        mockMvc.perform(post("/api/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("E-mail enviado com sucesso!"));

        // Verify que o service foi chamado com os parâmetros corretos
        verify(emailService, times(1)).sendSimpleEmail(
                "teste@exemplo.com",
                "Assunto de Teste",
                "Conteúdo do email de teste"
        );
    }

    @Test
    void testSendEmail_ServiceThrowsException() throws Exception {

        EmailController.EmailRequest emailRequest = new EmailController.EmailRequest();
        emailRequest.setTo("teste@exemplo.com");
        emailRequest.setSubject("Assunto de Teste");
        emailRequest.setContent("Conteúdo do email de teste");

        String requestJson = objectMapper.writeValueAsString(emailRequest);

        // Configurar o mock para lançar exceção
        doThrow(new RuntimeException("Erro de conexão SMTP"))
                .when(emailService)
                .sendSimpleEmail(anyString(), anyString(), anyString());


        mockMvc.perform(post("/api/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erro ao enviar e-mail: Erro de conexão SMTP"));

        // Verify que o service foi chamado
        verify(emailService, times(1)).sendSimpleEmail(
                "teste@exemplo.com",
                "Assunto de Teste",
                "Conteúdo do email de teste"
        );
    }

    @Test
    void testSendEmail_WithEmptyFields() throws Exception {

        EmailController.EmailRequest emailRequest = new EmailController.EmailRequest();
        emailRequest.setTo(""); // Campo vazio
        emailRequest.setSubject("");
        emailRequest.setContent("");

        String requestJson = objectMapper.writeValueAsString(emailRequest);


        mockMvc.perform(post("/api/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("E-mail enviado com sucesso!"));

        // Verify que o service foi chamado mesmo com campos vazios
        verify(emailService, times(1)).sendSimpleEmail("", "", "");
    }

    @Test
    void testSendEmail_WithNullFields() throws Exception {

        EmailController.EmailRequest emailRequest = new EmailController.EmailRequest();


        String requestJson = objectMapper.writeValueAsString(emailRequest);


        mockMvc.perform(post("/api/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("E-mail enviado com sucesso!"));

        // Verify que o service foi chamado com valores null
        verify(emailService, times(1)).sendSimpleEmail(null, null, null);
    }

    @Test
    void testSendEmail_InvalidJson() throws Exception {

        String invalidJson = "{ invalid json }";


        mockMvc.perform(post("/api/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

        // Verify que o service não foi chamado
        verify(emailService, never()).sendSimpleEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testSendEmail_EmailServiceThrowsMailException() throws Exception {

        EmailController.EmailRequest emailRequest = new EmailController.EmailRequest();
        emailRequest.setTo("email-invalido");
        emailRequest.setSubject("Teste");
        emailRequest.setContent("Conteúdo");

        String requestJson = objectMapper.writeValueAsString(emailRequest);

        // Simular uma exceção específica de email
        doThrow(new RuntimeException("Endereço de email inválido"))
                .when(emailService)
                .sendSimpleEmail(anyString(), anyString(), anyString());


        mockMvc.perform(post("/api/email/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erro ao enviar e-mail: Endereço de email inválido"));

        verify(emailService, times(1)).sendSimpleEmail(
                "email-invalido",
                "Teste",
                "Conteúdo"
        );
    }

    @Test
    void testEmailRequest_GettersAndSetters() {
        // Arrange & Act
        EmailController.EmailRequest emailRequest = new EmailController.EmailRequest();
        emailRequest.setTo("teste@exemplo.com");
        emailRequest.setSubject("Assunto Teste");
        emailRequest.setContent("Conteúdo Teste");

        // Assert
        assert emailRequest.getTo().equals("teste@exemplo.com");
        assert emailRequest.getSubject().equals("Assunto Teste");
        assert emailRequest.getContent().equals("Conteúdo Teste");
    }
}