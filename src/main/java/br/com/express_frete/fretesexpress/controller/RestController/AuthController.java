package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.dto.AuthRequest;
import br.com.express_frete.fretesexpress.dto.AuthResponse;
import br.com.express_frete.fretesexpress.dto.TokenValidationResponse;
import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.repository.UserRepository;
import br.com.express_frete.fretesexpress.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest authRequest) {
        Optional<User> userOpt = verifyUser(authRequest.getLogin());
        if (userOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        User user = userOpt.get();

        // Verify password using bcrypt
        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        // Generate JWT token
        String token = jwtService.generateToken(user);

        // Create response object
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());

        return ResponseEntity.ok(response);
    }

    private Optional<User> verifyUser(String login) {
        // First try to find by email
        Optional<User> userOpt = userRepository.findByEmail(login);

        if (userOpt.isPresent()) {
            return userOpt;
        }

        // If not found by email, try by CPF/CNPJ
        userOpt = userRepository.findByCpf_cnpj(login);

        if (userOpt.isPresent()) {
            return userOpt;
        }

        // If not found by CPF/CNPJ, try by username
        return userRepository.findByUsername(login);
    }

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        // Remove "Bearer " prefix if exists
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        try {
            // Validate token
            Claims claims = jwtService.validateToken(token);

            // If we got here, token is valid
            TokenValidationResponse response = new TokenValidationResponse();
            response.setValid(true);
            response.setUserId(Long.parseLong(claims.getSubject()));
            response.setEmail(claims.get("email", String.class));
            response.setRole(claims.get("role", String.class));

            return ResponseEntity.ok(response);
        } catch (JwtException e) {
            // Invalid token
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", "Invalid or expired token");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}