package br.com.express_frete.fretesexpress.controller;

import br.com.express_frete.fretesexpress.dto.LoginRequestDTO;
import br.com.express_frete.fretesexpress.dto.LoginResponseDTO;
import br.com.express_frete.fretesexpress.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        logger.info("Requisição de login recebida para e-mail: {}", loginRequest.getEmail());
        LoginResponseDTO response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @RequestParam String email,
            @RequestParam String senha,
            @RequestParam String role) {
        logger.info("Requisição de registro recebida para e-mail: {}", email);
        authService.registrarUsuario(email, senha, role);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @ExceptionHandler({BadCredentialsException.class, IllegalStateException.class})
    public ResponseEntity<String> handleAuthenticationExceptions(Exception ex) {
        logger.error("Erro de autenticação: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"" + ex.getMessage() + "\"}");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        logger.error("Erro interno no servidor: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"Erro interno no servidor\"}");
    }
}