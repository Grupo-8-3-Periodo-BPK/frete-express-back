package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.model.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private User user;

    @BeforeEach
    public void setup() {
        // Configura uma chave secreta fixa para os testes
        ReflectionTestUtils.setField(jwtService, "secret", "b7a3161ef64b7ea4afb344ba5a121e7f69083e4ff3aaae402d5dd65f500f6d2c");
        ReflectionTestUtils.setField(jwtService, "expirationTime", 86400000L);

        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setRole(Role.CLIENT);
    }

    @Test
    public void testGenerateToken_Success() {
        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    public void testGenerateToken_InvalidUser() {
        assertThrows(IllegalArgumentException.class, () -> jwtService.generateToken(null));

        User invalidUser = new User();
        assertThrows(IllegalArgumentException.class, () -> jwtService.generateToken(invalidUser));
    }

    @Test
    public void testValidateToken_Success() {
        String token = jwtService.generateToken(user);
        Claims claims = jwtService.validateToken(token);

        assertNotNull(claims);
        assertEquals(user.getId().toString(), claims.getSubject());
        assertEquals(user.getName(), claims.get("name"));
        assertEquals(user.getEmail(), claims.get("email"));
        assertEquals(user.getRole().toString(), claims.get("role"));
    }

    @Test
    public void testValidateToken_InvalidToken() {
        assertThrows(JwtException.class, () -> jwtService.validateToken(null));
        assertThrows(JwtException.class, () -> jwtService.validateToken(""));
        assertThrows(JwtException.class, () -> jwtService.validateToken("invalid.token.here"));
    }

    @Test
    public void testIsTokenValid() {
        String validToken = jwtService.generateToken(user);
        assertTrue(jwtService.isTokenValid(validToken));

        assertFalse(jwtService.isTokenValid(null));
        assertFalse(jwtService.isTokenValid(""));
        assertFalse(jwtService.isTokenValid("invalid.token.here"));
    }

    @Test
    public void testGetUserIdFromToken() {
        String token = jwtService.generateToken(user);
        Long userId = jwtService.getUserIdFromToken(token);

        assertEquals(user.getId(), userId);
        assertNull(jwtService.getUserIdFromToken("invalid.token"));
    }

    @Test
    public void testGetExpirationTime() {
        Instant expiration = jwtService.getExpirationTime();
        assertNotNull(expiration);
        assertTrue(expiration.isAfter(Instant.now()));
    }
}