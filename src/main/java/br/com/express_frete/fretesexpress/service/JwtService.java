package br.com.express_frete.fretesexpress.service;

import br.com.express_frete.fretesexpress.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for JWT token generation and validation
 */
@Service
public class JwtService {

    // Token expiration time in milliseconds (24 hours)
    @Value("${jwt.expiration:86400000}")
    private long expirationTime;

    private String secret;

    @PostConstruct
    public void init() {
        try {
            // Try to read the JWT key from the .env file through the environment variable
            secret = System.getenv("JWT_SECRET");

            // If not found, use a default value for development
            if (secret == null || secret.trim().isEmpty()) {
                System.out.println(
                        "[WARNING] JWT_SECRET variable not found in environment. Using default value for development.");
                secret = "8fhg73h2g9b219g1b394hg1b37g19b3g173g17";
            } else {
                // Remove possible quotes and extra spaces from the value
                secret = secret.trim().replaceAll("^[\"']|[\"']$", "");
            }
        } catch (Exception e) {
            System.out.println("[WARNING] Error accessing JWT_SECRET variable: " + e.getMessage());
            System.out.println("[WARNING] Using default value for development.");
            secret = "8fhg73h2g9b219g1b394hg1b37g19b3g173g17";
        }
    }

    /**
     * Generates a JWT token based on user information
     *
     * @param user user for which the token will be generated
     * @return JWT token
     */
    public String generateToken(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("Usuário inválido para geração de token");
        }

        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", user.getId());
            claims.put("email", user.getEmail());
            claims.put("role", user.getRole() != null ? user.getRole().toString() : "USER");

            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getId().toString())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            System.out.println("Erro ao gerar token: " + e.getMessage());
            throw new RuntimeException("Erro ao gerar token JWT", e);
        }
    }

    /**
     * Validates a JWT token
     *
     * @param token token to be validated
     * @return token claims if valid
     * @throws JwtException if the token is invalid or expired
     */
    public Claims validateToken(String token) throws JwtException {
        if (token == null || token.isEmpty()) {
            throw new JwtException("Token vazio ou nulo");
        }

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            System.out.println("Erro na validação do token: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("Erro inesperado na validação do token: " + e.getMessage());
            throw new JwtException("Erro ao validar token: " + e.getMessage());
        }
    }

    /**
     * Gets the signing key based on the secret
     *
     * @return signing key
     */
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}