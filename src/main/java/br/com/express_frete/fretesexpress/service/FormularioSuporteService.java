package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.model.FormularioSuporte;
import br.com.express_frete.fretesexpress.repository.FormularioSuporteRepository;
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
public class FormularioSuporteService {

    private static final Logger logger = LoggerFactory.getLogger(FormularioSuporteService.class);

    @Autowired
    private FormularioSuporteRepository formularioSuporteRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Value("${suporte.email.destino}")
    private String emailDestino;

    @Value("${spring.mail.host}")
    private String mailHost;

    public FormularioSuporteService() {
        logger.info("Inicializando FormularioSuporteService com mail host: {}", mailHost);
    }

    public FormularioSuporte salvar(FormularioSuporte formularioSuporte) {
        logger.info("Salvando formulario de suporte para: {}", formularioSuporte.getNomeSolicitante());
        formularioSuporte.setDataEnvio(LocalDateTime.now()); // Define a data atual
        FormularioSuporte salvo = formularioSuporteRepository.save(formularioSuporte);
        enviarEmail(formularioSuporte);
        return salvo;
    }

    public Optional<FormularioSuporte> buscarPorId(Long id) {
        return formularioSuporteRepository.findById(id);
    }

    public List<FormularioSuporte> listarTodos() {
        return formularioSuporteRepository.findAll();
    }

    private void enviarEmail(FormularioSuporte formulario) {
        logger.info("Enviando e-mail para {} com assunto: {}", emailDestino, formulario.getAssunto());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailDestino);
        message.setSubject("Novo Formulario de Suporte: " + formulario.getAssunto());
        message.setText("Novo formulario recebido:\n\n" +
                "Nome: " + formulario.getNomeSolicitante() + "\n" +
                "E-mail: " + formulario.getEmailRetorno() + "\n" +
                "Assunto: " + formulario.getAssunto() + "\n" +
                "Descricao: " + formulario.getDescricaoProblema() + "\n" +
                "Data de Envio: " + formulario.getDataEnvio());
        message.setFrom("noreply@expressfrete.com");
        try {
            mailSender.send(message);
            logger.info("E-mail enviado com sucesso para {}", emailDestino);
        } catch (Exception e) {
            logger.error("Erro ao enviar e-mail: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao enviar e-mail", e);
        }
    }
}