package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.dto.UserUpdateDTO;
import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.model.enums.Role;
import br.com.express_frete.fretesexpress.service.UserService;
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

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testFindAll() throws Exception {
        when(userService.findAll()).thenReturn(Collections.singletonList(new User()));
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void testFindById_Success() throws Exception {
        User user = new User();
        user.setId(1L);
        when(userService.findByIdOptional(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testFindById_NotFound() throws Exception {
        when(userService.findByIdOptional(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testRegisterClient() throws Exception {
        User user = new User();
        user.setName("Test Client");
        user.setUsername("client@test.com");
        user.setEmail("client@test.com");
        user.setPassword("password123");
        user.setRole(Role.CLIENT);

        when(userService.save(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/users/register/client")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());
    }

    @Test
    void testRegisterDriver() throws Exception {
        User user = new User();
        user.setName("Test Driver");
        user.setUsername("driver@test.com");
        user.setEmail("driver@test.com");
        user.setPassword("password123");
        user.setRole(Role.DRIVER);

        when(userService.save(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/api/users/register/driver")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated());
    }

    @Test
    void testUpdateUser() throws Exception {
        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setName("New Name");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("New Name");

        when(userService.update(eq(1L), any(UserUpdateDTO.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));
    }

    @Test
    void testDeleteUser() throws Exception {
        when(userService.findByIdOptional(1L)).thenReturn(Optional.of(new User()));
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).delete(1L);
    }
}
