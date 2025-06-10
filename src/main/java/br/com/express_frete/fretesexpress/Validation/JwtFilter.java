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
            String requestUri = request.getRequestURI();

            if (requestUri.startsWith("/api/auth/") ||
                    requestUri.startsWith("/swagger-ui/") ||
                    requestUri.startsWith("/v3/api-docs/")) {

                filterChain.doFilter(request, response);
                return;
            }

            String token = extractTokenFromRequest(request);

            if (token != null) {
                if (jwtService.isTokenValid(token)) {
                    try {
                        Claims claims = jwtService.validateToken(token);

                        Long userId = Long.parseLong(claims.getSubject());
                        String email = claims.get("email", String.class);
                        String role = claims.get("role", String.class);

                        Optional<User> userOpt = userRepository.findById(userId);
                        if (userOpt.isPresent()) {
                            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    userId, null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + role)));

                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            System.out.println("Usuário autenticado: " + email + " com função: " + role);

                            filterChain.doFilter(request, response);
                            return;
                        } else {
                            handleAuthenticationFailure(response, "Usuário não encontrado");
                            return;
                        }
                    } catch (JwtException e) {
                        handleAuthenticationFailure(response, "Token inválido: " + e.getMessage());
                        return;
                    } catch (Exception e) {
                        handleAuthenticationFailure(response, "Erro ao processar token: " + e.getMessage());
                        return;
                    }
                } else {
                    handleAuthenticationFailure(response, "Token expirado ou inválido");
                    return;
                }
            } else {
                handleAuthenticationFailure(response, "Token de autenticação não encontrado");
                return;
            }
        } catch (Exception e) {
            System.out.println("Erro no filtro JWT: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    //Extrai o token do cabeçalho de autorização ou cookies
    private String extractTokenFromRequest(HttpServletRequest request) {
        // Primeiro tenta extrair do cabeçalho Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("Token extraído do cabeçalho Authorization");
            return token;
        }

        // Se não encontrou no cabeçalho, tenta extrair dos cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwt_token".equals(cookie.getName())) {
                    System.out.println("Token extraído do cookie jwt_token");
                    return cookie.getValue();
                }
            }

            // Imprimir todos os cookies disponíveis para debug
            System.out.println("Cookies disponíveis: ");
            for (Cookie cookie : cookies) {
                System.out.println("  - " + cookie.getName() + ": "
                        + cookie.getValue().substring(0, Math.min(10, cookie.getValue().length())) + "...");
            }
        } else {
            System.out.println("Nenhum cookie encontrado no request");
        }

        return null;
    }

    // Método para lidar com falhas de autenticação de maneira padronizada
    private void handleAuthenticationFailure(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\": \"" + message + "\"}");
    }
}