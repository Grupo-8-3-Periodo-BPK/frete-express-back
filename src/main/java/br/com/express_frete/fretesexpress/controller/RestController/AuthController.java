package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.dto.AuthRequest;
import br.com.express_frete.fretesexpress.dto.AuthResponse;
import br.com.express_frete.fretesexpress.dto.TokenValidationResponse;
import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.repository.UserRepository;
import br.com.express_frete.fretesexpress.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest authRequest, HttpServletResponse response) {
        try {
            // Verify if the user exists
            Optional<User> userOpt = verifyUser(authRequest.getLogin());
            if (userOpt.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            User user = userOpt.get();
            // Verify if the password is correct
            if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            // Gerar o token JWT
            String token = jwtService.generateToken(user);

            // Calcular data de expiração (24 horas)
            Instant expiration = jwtService.getExpirationTime();

            // Configurar cookie para desenvolvimento local
            Cookie jwtCookie = new Cookie("jwt_token", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false); // false para HTTP local
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(24 * 60 * 60); // 24 horas

            // Adicionar cookie à resposta
            response.addCookie(jwtCookie);

            // Log para debug
            System.out.println(
                    "Cookie JWT configurado: " + jwtCookie.getName() + "=" + token.substring(0, 20) + "... (truncado)");

            // Create response without including the token
            AuthResponse authResponse = new AuthResponse();
            authResponse.setId(user.getId());
            authResponse.setName(user.getName());
            authResponse.setEmail(user.getEmail());
            authResponse.setRole(user.getRole());
            authResponse.setExpiration(expiration);

            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            System.out.println("Error in login: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Remove the cookie
            Cookie cookie = new Cookie("jwt_token", null);
            cookie.setHttpOnly(true);
            cookie.setSecure(false); // Mesma configuração do login
            cookie.setPath("/");
            cookie.setMaxAge(0); // Remove the cookie
            response.addCookie(cookie);

            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("message", "Logout successful");
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            System.out.println("Error in logout: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error performing logout");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        try {
            String token = extractToken(request);
            if (token == null) {
                // Try to extract from cookies
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if ("jwt_token".equals(cookie.getName())) {
                            token = cookie.getValue();
                            break;
                        }
                    }
                }

                if (token == null) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("valid", false);
                    response.put("message", "Token not provided or invalid format");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }
            }

            // Verify if the JWT token is valid
            if (!jwtService.isTokenValid(token)) {
                Map<String, Object> response = new HashMap<>();
                response.put("valid", false);
                response.put("message", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Validate the JWT token
            Claims claims = jwtService.validateToken(token);

            TokenValidationResponse response = new TokenValidationResponse();
            response.setValid(true);
            response.setUserId(Long.parseLong(claims.getSubject()));
            response.setName(claims.get("name", String.class));
            response.setEmail(claims.get("email", String.class));
            response.setRole(claims.get("role", String.class));

            return ResponseEntity.ok(response);
        } catch (JwtException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());

            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", "Invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            System.out.println("Error validating token: " + e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", "Error validating token");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private String extractToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private Optional<User> verifyUser(String login) {
        // Try to find the user by email
        Optional<User> userOpt = userRepository.findByEmail(login);
        if (userOpt.isPresent())
            return userOpt;

        // Try to find the user by CPF/CNPJ
        userOpt = userRepository.findByCpf_cnpj(login);
        if (userOpt.isPresent())
            return userOpt;

        // Try to find the user by username
        return userRepository.findByUsername(login);
    }
}