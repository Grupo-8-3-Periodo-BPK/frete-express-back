<<<<<<< HEAD
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

=======
package br.com.express_frete.fretesexpress.controller.RestController;

import br.com.express_frete.fretesexpress.dto.AuthRequest;
import br.com.express_frete.fretesexpress.dto.AuthResponse;
import br.com.express_frete.fretesexpress.dto.TokenValidationResponse;
import br.com.express_frete.fretesexpress.model.Token;
import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.repository.UserRepository;
import br.com.express_frete.fretesexpress.service.JwtService;
import br.com.express_frete.fretesexpress.service.TokenService;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

>>>>>>> 4bf44944750ccbe666555978c12fbe31d496fb9a
@RestController
@RequestMapping("/api/auth")
public class AuthController {

<<<<<<< HEAD
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
=======
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TokenService tokenService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest authRequest, HttpServletResponse response) {
        try {
            // Verificar se o usuário existe
            Optional<User> userOpt = verifyUser(authRequest.getLogin());
            if (userOpt.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            User user = userOpt.get();
            // Verificar se a senha está correta
            if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            // Criar token e salvá-lo no banco
            Token token = tokenService.createToken(user);

            // Configurar o cookie seguro
            Cookie jwtCookie = new Cookie("jwt_token", token.getToken());
            jwtCookie.setHttpOnly(true); // Impede acesso via JavaScript
            jwtCookie.setSecure(true); // Envia apenas em HTTPS
            jwtCookie.setPath("/"); // Disponível em todo o site
            jwtCookie.setMaxAge(24 * 60 * 60); // Duração de 24 horas em segundos
            response.addCookie(jwtCookie);

            // Criar resposta sem incluir o token
            AuthResponse authResponse = new AuthResponse();
            authResponse.setId(user.getId());
            authResponse.setName(user.getName());
            authResponse.setEmail(user.getEmail());
            authResponse.setRole(user.getRole());
            authResponse.setExpiration(token.getExpiration());
            

            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            System.out.println("Erro no login: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", "Erro interno no servidor");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        try {
            String token = extractTokenFromRequest(request);
            if (token != null) {
                tokenService.invalidateToken(token);
                System.out.println("Token invalidado com sucesso");
            } else {
                System.out.println("Tentativa de logout sem token");
            }

            // Remover o cookie
            Cookie cookie = new Cookie("jwt_token", null);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(0); // Remove o cookie
            response.addCookie(cookie);

            Map<String, String> responseMap = new HashMap<>();
            responseMap.put("message", "Logout successful");
            return ResponseEntity.ok(responseMap);
        } catch (Exception e) {
            System.out.println("Erro no logout: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", "Erro ao realizar logout");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        try {
            String token = extractTokenFromRequest(request);
            if (token == null) {
                // Tentar extrair de cookies
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
                    response.put("message", "Token não fornecido ou formato inválido");
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
                }
            }

            // Verificar se o token existe e é válido no banco
            if (!tokenService.isTokenValid(token)) {
                Map<String, Object> response = new HashMap<>();
                response.put("valid", false);
                response.put("message", "Token inválido ou expirado");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Validar o token JWT
            Claims claims = jwtService.validateToken(token);

            TokenValidationResponse response = new TokenValidationResponse();
            response.setValid(true);
            response.setUserId(Long.parseLong(claims.getSubject()));
            response.setEmail(claims.get("email", String.class));
            response.setRole(claims.get("role", String.class));

            return ResponseEntity.ok(response);
        } catch (JwtException e) {
            System.out.println("Token JWT inválido: " + e.getMessage());
            String token = extractTokenFromRequest(request);
            if (token != null) {
                tokenService.invalidateToken(token);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", "Token inválido");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            System.out.println("Erro ao validar token: " + e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", "Erro ao validar token");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Extrai o token do cabeçalho de autorização
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        // Primeiro tenta extrair do cabeçalho Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        // Se não encontrou no cabeçalho, tenta extrair dos cookies
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

    /**
     * Verifica se o usuário existe buscando por diferentes campos
     */
    private Optional<User> verifyUser(String login) {
        // Primeiro tenta encontrar por email
        Optional<User> userOpt = userRepository.findByEmail(login);

        if (userOpt.isPresent()) {
            return userOpt;
        }

        // Se não encontrou por email, tenta por CPF/CNPJ
        userOpt = userRepository.findByCpf_cnpj(login);

        if (userOpt.isPresent()) {
            return userOpt;
        }

        // Se não encontrou por CPF/CNPJ, tenta por username
        return userRepository.findByUsername(login);
>>>>>>> 4bf44944750ccbe666555978c12fbe31d496fb9a
    }
}