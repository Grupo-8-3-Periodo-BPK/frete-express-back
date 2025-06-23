package br.com.express_frete.fretesexpress.controller.RestController;


import br.com.express_frete.fretesexpress.controller.RestController.UserController;
import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.model.enums.Role;
import br.com.express_frete.fretesexpress.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = {UserController.class})
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/users - Deve retornar todos os usuários")
    void testFindAll() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("teste");

        Mockito.when(userService.findAll()).thenReturn(Arrays.asList(user));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("teste"));
    }

    @Test
    @DisplayName("GET /api/users/paged - Deve retornar usuários paginados")
    void testFindAllPaged() throws Exception {
        User user = new User();
        user.setUsername("teste");

        Mockito.when(userService.findAllPaged(any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(user)));

        mockMvc.perform(get("/api/users/paged"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].username").value("teste"));
    }

    @Test
    @DisplayName("GET /api/users/1 - Deve retornar usuário por ID")
    void testFindById() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("usuario1");

        Mockito.when(userService.findByIdOptional(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("usuario1"));
    }

    @Test
    void testRegisterClient() throws Exception {
        User user = new User();
        user.setName("Tales");
        user.setUsername("talesuriel"); // <- campo obrigatório
        user.setEmail("tales@email.com");
        user.setPassword("123456");

        String userJson = objectMapper.writeValueAsString(user);

        mockMvc.perform(post("/api/users/register/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated());
    }



    @Test
    @DisplayName("DELETE /api/users/1 - Deve remover usuário existente")
    void testDelete() throws Exception {
        User user = new User();
        user.setId(1L);

        Mockito.when(userService.findByIdOptional(1L)).thenReturn(Optional.of(user));
        Mockito.doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void InvalidCpfProvided() throws Exception {
        String customerJson = """
            {
                "name": "João Silva",
                "email": "joao@email.com",
                "cpf": "12345678900",
                "password": "password123"
            }
        """;

        mockMvc.perform(post("/api/users/register/client")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("CPF Inválido."));
    }
}
