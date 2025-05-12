package br.com.express_frete.fretesexpress.service;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class EmailServiceTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("teste", "teste"))
            .withPerMethodLifecycle(false);

    @Autowired
    private EmailService emailService;

    @Test
    public void testEnviarEmail() throws MessagingException {
        // Dados de teste
        String to = "destinatario@exemplo.com";
        String subject = "Teste de Configuração de E-mail";
        String content = "Este é um e-mail de teste para verificar se a configuração de e-mail está funcionando corretamente.";

        // Enviar e-mail
        emailService.sendSimpleEmail(to, subject, content);

        // Verificar se o e-mail foi recebido pelo GreenMail
        assertTrue(greenMail.waitForIncomingEmail(5000, 1), "E-mail não foi recebido");

        // Recuperar e verificar a mensagem
        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length, "Número de e-mails recebidos não corresponde ao esperado");

        MimeMessage message = messages[0];
        assertEquals(subject, message.getSubject(), "Assunto do e-mail não corresponde");
        assertEquals(to, message.getAllRecipients()[0].toString(), "Destinatário não corresponde");

        // Converter o conteúdo da mensagem para String e comparar
        String body = GreenMailUtil.getBody(message);
        assertEquals(content, body, "Conteúdo do e-mail não corresponde");
    }
}

// Classe utilitária para acessar o corpo do e-mail
class GreenMailUtil {
    public static String getBody(MimeMessage message) throws MessagingException {
        try {
            return message.getContent().toString();
        } catch (Exception e) {
            return "";
        }
    }
}