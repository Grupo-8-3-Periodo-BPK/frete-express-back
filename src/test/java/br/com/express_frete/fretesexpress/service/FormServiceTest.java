package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.model.Form;
import br.com.express_frete.fretesexpress.repository.FormRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FormServiceTest {

    @InjectMocks
    private FormService formService;

    @Mock
    private FormRepository formRepository;

    @Mock
    private JavaMailSender mailSender;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        setPrivateField(formService, "formRepository", formRepository);
        setPrivateField(formService, "mailSender", mailSender);
        setPrivateField(formService, "mailDestination", "suporte@email.com");
        setPrivateField(formService, "mailHost", "smtp.email.com");
    }

    @Test
    void testSaveFormSendsEmailAndSaves() {
        Form form = new Form();
        form.setNameRequester("Tales");
        form.setReturnEmail("tales@email.com");
        form.setSubject("Ajuda");
        form.setProblemDescription("Teste de problema");

        when(formRepository.save(any(Form.class))).thenReturn(form);

        Form result = formService.save(form);

        assertNotNull(result);
        verify(formRepository).save(form);
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void testFindById() {
        Form form = new Form();
        form.setId(1L);

        when(formRepository.findById(1L)).thenReturn(Optional.of(form));

        Optional<Form> result = formService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    // Utilitário para injeção via reflection
    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
