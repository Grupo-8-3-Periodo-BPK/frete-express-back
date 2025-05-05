package br.com.express_frete.fretesexpress.controller;

import br.com.express_frete.fretesexpress.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody EmailRequest request) {
        try {
            emailService.sendSimpleEmail(
                    request.getTo(),
                    request.getSubject(),
                    request.getContent());
            return ResponseEntity.ok("E-mail enviado com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao enviar e-mail: " + e.getMessage());
        }
    }

    // Classe para receber os dados do e-mail via JSON
    public static class EmailRequest {
        private String to;
        private String subject;
        private String content;

        // Getters e Setters
        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}