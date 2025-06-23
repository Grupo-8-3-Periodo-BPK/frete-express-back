package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.Validation.DriverValidation;
import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.model.enums.Role;
import br.com.express_frete.fretesexpress.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Validator validator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        List<User> users = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.findAll();
        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void testFindAllPaged() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<User> page = new PageImpl<>(List.of(new User(), new User()));
        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<User> result = userService.findAllPaged(pageable);
        assertEquals(2, result.getContent().size());
    }

    @Test
    void testFindByRole() {
        Role role = Role.CLIENT;
        List<User> users = List.of(new User(), new User());
        when(userRepository.findByRole(role)).thenReturn(users);

        List<User> result = userService.findByRole(role);
        assertEquals(2, result.size());
    }

    @Test
    void testFindByRolePaged() {
        Role role = Role.CLIENT;
        Pageable pageable = PageRequest.of(0, 1);
        Page<User> page = new PageImpl<>(List.of(new User()));
        when(userRepository.findByRole(role, pageable)).thenReturn(page);

        Page<User> result = userService.findByRolePaged(role, pageable);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void testFindByIdFound() {
        User user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);
        assertNotNull(result);
    }

    @Test
    void testFindByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.findById(1L));
    }

    @Test
    void testSaveWithDriverRoleAndValid() {
        User user = new User();
        user.setRole(Role.DRIVER);
        user.setPassword("123456");

        when(validator.validate(any(User.class), eq(DriverValidation.class))).thenReturn(Set.of());
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.save(user);
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testDelete() {
        Long id = 1L;
        doNothing().when(userRepository).deleteById(id);

        userService.delete(id);
        verify(userRepository).deleteById(id);
    }

    @Test
    void testUpdateUser() {
        Long id = 1L;
        User existingUser = new User();
        existingUser.setId(id);

        User updatedData = new User();
        updatedData.setName("Novo Nome");
        updatedData.setEmail("novo@email.com");
        updatedData.setPassword("novaSenha");
        updatedData.setRole(Role.CLIENT);
        updatedData.setCpfCnpj("00000000000");
        updatedData.setPhone("999999999");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        User result = userService.update(id, updatedData);

        assertEquals("Novo Nome", result.getName());
        assertEquals("novo@email.com", result.getEmail());
        assertEquals("00000000000", result.getCpfCnpj());
        assertEquals("999999999", result.getPhone());
    }
}
