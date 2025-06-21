package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.dto.AuthRequest;
import br.com.express_frete.fretesexpress.dto.AuthResponse;
import br.com.express_frete.fretesexpress.dto.TokenValidationResponse;
import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.repository.UserRepository;
import br.com.express_frete.fretesexpress.service.JwtService;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private HttpServletRequest httpServletRequest;

    private final User mockUser = new User();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser.setId(1L);
        mockUser.setName("Test User");
        mockUser.setEmail("test@example.com");
        mockUser.setUsername("testuser");
        mockUser.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("password"));
        mockUser.setRole(br.com.express_frete.fretesexpress.model.enums.Role.CLIENT);
    }

    @Test
    void testLogin_Success() {
        AuthRequest request = new AuthRequest();
        request.setLogin("test@example.com");
        request.setPassword("password");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        when(jwtService.generateToken(mockUser)).thenReturn("valid-token");
        when(jwtService.getExpirationTime()).thenReturn(Instant.now().plusSeconds(86400));

        ResponseEntity<?> response = authController.login(request, httpServletResponse);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof AuthResponse);

        AuthResponse authResponse = (AuthResponse) response.getBody();
        assertEquals("Test User", authResponse.getName());
    }

    @Test
    void testLogin_InvalidPassword() {
        AuthRequest request = new AuthRequest();
        request.setLogin("test@example.com");
        request.setPassword("wrongpassword");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));

        ResponseEntity<?> response = authController.login(request, httpServletResponse);

        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void testLogout_Success() {
        ResponseEntity<?> response = authController.logout(httpServletRequest, httpServletResponse);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Logout successful"));
    }

    @Test
    void testValidateToken_Valid() {
        Cookie cookie = new Cookie("jwt_token", "valid-token");
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[]{cookie});
        when(jwtService.isTokenValid("valid-token")).thenReturn(true);

        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("1");
        when(claims.get("name", String.class)).thenReturn("Test User");
        when(claims.get("email", String.class)).thenReturn("test@example.com");
        when(claims.get("role", String.class)).thenReturn("CLIENT");
        when(jwtService.validateToken("valid-token")).thenReturn(claims);

        ResponseEntity<?> response = authController.validateToken(httpServletRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof TokenValidationResponse);

        TokenValidationResponse resp = (TokenValidationResponse) response.getBody();
        assertTrue(resp.isValid());
        assertEquals("Test User", resp.getName());
    }

    @Test
    void testValidateToken_Invalid() {
        Cookie cookie = new Cookie("jwt_token", "invalid-token");
        when(httpServletRequest.getCookies()).thenReturn(new Cookie[]{cookie});
        when(jwtService.isTokenValid("invalid-token")).thenReturn(false);

        ResponseEntity<?> response = authController.validateToken(httpServletRequest);

        assertEquals(401, response.getStatusCodeValue());
    }
}
