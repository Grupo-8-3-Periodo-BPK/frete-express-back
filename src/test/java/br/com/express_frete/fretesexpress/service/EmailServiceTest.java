package br.com.express_frete.fretesexpress.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.*;

public class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private JavaMailSender mailSender;

    @Captor
    private ArgumentCaptor<SimpleMailMessage> messageCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendSimpleEmail() {
        // Arrange
        String to = "teste@email.com";
        String subject = "Assunto do Email";
        String content = "Conteúdo do Email";

        // Act
        emailService.sendSimpleEmail(to, subject, content);

        // Assert
        verify(mailSender).send(messageCaptor.capture());
        SimpleMailMessage sentMessage = messageCaptor.getValue();

        assert sentMessage.getTo() != null;
        assert sentMessage.getTo()[0].equals(to);
        assert sentMessage.getSubject().equals(subject);
        assert sentMessage.getText().equals(content);
    }
}
