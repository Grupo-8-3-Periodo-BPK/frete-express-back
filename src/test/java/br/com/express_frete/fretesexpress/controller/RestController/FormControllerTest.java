package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.dto.FormDTO;
import br.com.express_frete.fretesexpress.model.Form;
import br.com.express_frete.fretesexpress.service.FormService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FormControllerTest {

    @Mock
    private FormService formService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private FormController formController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(formController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void testSend_Success() throws Exception {

        FormDTO formDTO = createValidFormDTO();
        Form form = createValidForm();
        Form savedForm = createValidForm();
        savedForm.setId(1L);
        FormDTO savedFormDTO = createValidFormDTO();
        savedFormDTO.setId(1L);

        when(modelMapper.map(formDTO, Form.class)).thenReturn(form);
        when(formService.save(any(Form.class))).thenReturn(savedForm);
        when(modelMapper.map(savedForm, FormDTO.class)).thenReturn(savedFormDTO);

        String requestJson = objectMapper.writeValueAsString(formDTO);


        mockMvc.perform(post("/api/forms/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name_requester").value("João Silva"))
                .andExpect(jsonPath("$.return_email").value("joao@teste.com"))
                .andExpect(jsonPath("$.subject").value("Problema com frete"))
                .andExpect(jsonPath("$.problem_description").value("Descrição do problema"));

        verify(modelMapper, times(1)).map(formDTO, Form.class);
        verify(formService, times(1)).save(any(Form.class));
        verify(modelMapper, times(1)).map(savedForm, FormDTO.class);
    }

    @Test
    void testSend_InvalidData_BlankName() throws Exception {

        FormDTO formDTO = createValidFormDTO();
        formDTO.setNameRequester(""); // Nome vazio

        String requestJson = objectMapper.writeValueAsString(formDTO);


        mockMvc.perform(post("/api/forms/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(formService, never()).save(any(Form.class));
    }

    @Test
    void testSend_InvalidData_InvalidEmail() throws Exception {

        FormDTO formDTO = createValidFormDTO();
        formDTO.setReturnEmail("email-invalido"); // Email inválido

        String requestJson = objectMapper.writeValueAsString(formDTO);


        mockMvc.perform(post("/api/forms/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(formService, never()).save(any(Form.class));
    }

    @Test
    void testSend_InvalidData_BlankSubject() throws Exception {

        FormDTO formDTO = createValidFormDTO();
        formDTO.setSubject(""); // Subject vazio

        String requestJson = objectMapper.writeValueAsString(formDTO);


        mockMvc.perform(post("/api/forms/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(formService, never()).save(any(Form.class));
    }

    @Test
    void testSend_InvalidData_BlankProblemDescription() throws Exception {

        FormDTO formDTO = createValidFormDTO();
        formDTO.setProblemDescription(""); // Descrição vazia

        String requestJson = objectMapper.writeValueAsString(formDTO);


        mockMvc.perform(post("/api/forms/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(formService, never()).save(any(Form.class));
    }


    @Test
    void testFindById_Success() throws Exception {

        Long formId = 1L;
        Form form = createValidForm();
        form.setId(formId);
        FormDTO formDTO = createValidFormDTO();
        formDTO.setId(formId);

        when(formService.findById(formId)).thenReturn(Optional.of(form));
        when(modelMapper.map(form, FormDTO.class)).thenReturn(formDTO);


        mockMvc.perform(get("/api/forms/{id}", formId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(formId))
                .andExpect(jsonPath("$.name_requester").value("João Silva"))
                .andExpect(jsonPath("$.return_email").value("joao@teste.com"));

        verify(formService, times(1)).findById(formId);
        verify(modelMapper, times(1)).map(form, FormDTO.class);
    }

    @Test
    void testFindById_NotFound() throws Exception {

        Long formId = 999L;
        when(formService.findById(formId)).thenReturn(Optional.empty());


        mockMvc.perform(get("/api/forms/{id}", formId))
                .andExpect(status().isNotFound());

        verify(formService, times(1)).findById(formId);
        verify(modelMapper, never()).map(any(), any());
    }

    @Test
    void testFindAll_Success() throws Exception {

        Form form1 = createValidForm();
        form1.setId(1L);
        Form form2 = createValidForm();
        form2.setId(2L);
        form2.setNameRequester("Maria Santos");
        form2.setReturnEmail("maria@teste.com");

        List<Form> forms = Arrays.asList(form1, form2);

        FormDTO formDTO1 = createValidFormDTO();
        formDTO1.setId(1L);
        FormDTO formDTO2 = createValidFormDTO();
        formDTO2.setId(2L);
        formDTO2.setNameRequester("Maria Santos");
        formDTO2.setReturnEmail("maria@teste.com");

        when(formService.findAll()).thenReturn(forms);
        when(modelMapper.map(form1, FormDTO.class)).thenReturn(formDTO1);
        when(modelMapper.map(form2, FormDTO.class)).thenReturn(formDTO2);


        mockMvc.perform(get("/api/forms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name_requester").value("João Silva"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name_requester").value("Maria Santos"));

        verify(formService, times(1)).findAll();
        verify(modelMapper, times(1)).map(form1, FormDTO.class);
        verify(modelMapper, times(1)).map(form2, FormDTO.class);
    }

    @Test
    void testFindAll_EmptyList() throws Exception {

        when(formService.findAll()).thenReturn(Arrays.asList());


        mockMvc.perform(get("/api/forms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(formService, times(1)).findAll();
    }

    @Test
    void testSend_MaxLengthValidation() throws Exception {

        FormDTO formDTO = createValidFormDTO();
        // Nome com mais de 100 caracteres
        formDTO.setNameRequester("a".repeat(101));

        String requestJson = objectMapper.writeValueAsString(formDTO);


        mockMvc.perform(post("/api/forms/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(formService, never()).save(any(Form.class));
    }

    @Test
    void testSend_EmailMaxLengthValidation() throws Exception {

        FormDTO formDTO = createValidFormDTO();
        // Email com mais de 100 caracteres
        formDTO.setReturnEmail("a".repeat(95) + "@teste.com"); // > 100 chars

        String requestJson = objectMapper.writeValueAsString(formDTO);


        mockMvc.perform(post("/api/forms/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(formService, never()).save(any(Form.class));
    }

    @Test
    void testSend_SubjectMaxLengthValidation() throws Exception {

        FormDTO formDTO = createValidFormDTO();
        // Subject com mais de 200 caracteres
        formDTO.setSubject("a".repeat(201));

        String requestJson = objectMapper.writeValueAsString(formDTO);


        mockMvc.perform(post("/api/forms/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verify(formService, never()).save(any(Form.class));
    }

    // Métodos auxiliares para criar objetos de teste
    private FormDTO createValidFormDTO() {
        FormDTO formDTO = new FormDTO();
        formDTO.setNameRequester("João Silva");
        formDTO.setReturnEmail("joao@teste.com");
        formDTO.setSubject("Problema com frete");
        formDTO.setProblemDescription("Descrição do problema");
        formDTO.setSendingDate(LocalDateTime.now());
        return formDTO;
    }

    private Form createValidForm() {
        Form form = new Form();
        form.setNameRequester("João Silva");
        form.setReturnEmail("joao@teste.com");
        form.setSubject("Problema com frete");
        form.setProblemDescription("Descrição do problema");
        return form;
    }
}