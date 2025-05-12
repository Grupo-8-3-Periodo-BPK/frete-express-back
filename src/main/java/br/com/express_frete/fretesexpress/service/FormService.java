package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.model.Form;
import br.com.express_frete.fretesexpress.repository.FormRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FormService {

    private static final Logger logger = LoggerFactory.getLogger(FormService.class);

    @Autowired
    private FormRepository formRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${support.email.destination}")
    private String mailDestination;

    @Value("${spring.mail.host}")
    private String mailHost;

    public FormService() {
        logger.info("Initializing FormService with mail host: {}", mailHost);
    }

    public Form save(Form form) {
        logger.info("Saving support form for: {}", form.getNameRequester());
        form.setSendingDate(LocalDateTime.now());
        Form saved = formRepository.save(form);
        sendEmail(form);
        return saved;
    }

    public Optional<Form> findById(Long id) {
        return formRepository.findById(id);
    }

    public List<Form> findAll() {
        return formRepository.findAll();
    }

    private void sendEmail(Form form) {
        logger.info("Sending email to {} with subject: {}", mailDestination, form.getSubject());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(mailDestination);
        message.setSubject("New Support Form: " + form.getSubject());
        message.setText("New form received:\n\n" +
                "Name: " + form.getNameRequester() + "\n" +
                "Email: " + form.getReturnEmail() + "\n" +
                "Subject: " + form.getSubject() + "\n" +
                "Description: " + form.getProblemDescription() + "\n" +
                "Sending Date: " + form.getSendingDate());
        message.setFrom("noreply@expressfrete.com");
        try {
            mailSender.send(message);
            logger.info("Email sent successfully to {}", mailDestination);
        } catch (Exception e) {
            logger.error("Error sending email: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}