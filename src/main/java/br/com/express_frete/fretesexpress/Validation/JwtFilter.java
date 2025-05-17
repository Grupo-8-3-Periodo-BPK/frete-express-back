package br.com.express_frete.fretesexpress.Validation;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.express_frete.fretesexpress.model.User;
import br.com.express_frete.fretesexpress.repository.UserRepository;
import br.com.express_frete.fretesexpress.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filtro para validação de tokens JWT.
 * Este filtro verifica a validade do token JWT antes de permitir
 * o acesso aos endpoints protegidos.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // Extrair token do cabeçalho de autorização ou cookies
            String token = extractTokenFromRequest(request);

            if (token != null) {
                // Verificar se o token JWT é válido
                if (jwtService.isTokenValid(token)) {
                    try {
                        // Validar o JWT e extrair as claims
                        Claims claims = jwtService.validateToken(token);

                        // Extrair informações do usuário
                        Long userId = Long.parseLong(claims.getSubject());
                        String email = claims.get("email", String.class);
                        String role = claims.get("role", String.class);

                        // Buscar usuário do banco de dados para garantir que ainda existe
                        Optional<User> userOpt = userRepository.findById(userId);
                        if (userOpt.isPresent()) {
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    userId, null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + role)));

                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            System.out.println("Usuário autenticado: " + email + " com função: " + role);
                        }
                    } catch (JwtException e) {
                        System.out.println("Token JWT inválido: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Erro ao processar token: " + e.getMessage());
                    }
                } else {
                    System.out.println("Token JWT inválido");
                }
            }
        } catch (Exception e) {
            // Garantir que o filtro não quebre a cadeia de filtros
            System.out.println("Erro no filtro JWT: " + e.getMessage());
        }

        // Sempre continuar a cadeia de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Extrai o token do cabeçalho de autorização ou cookies
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
}
