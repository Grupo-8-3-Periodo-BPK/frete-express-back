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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class JwtService {

    @Value("${jwt.expiration:86400000}")
    private long expirationTime;

    private String secret;

    @PostConstruct
    public void init() {
        try {
            File envFile = new File(".env");
            if (envFile.exists()) {
                Properties props = new Properties();
                try (BufferedReader reader = new BufferedReader(new FileReader(envFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("JWT_SECRET=")) {
                            secret = line.substring("JWT_SECRET=".length());
                            break;
                        }
                    }
                }
            }
            if (secret == null || secret.trim().isEmpty()) {
                secret = System.getenv("JWT_SECRET");
            }

            if (secret == null || secret.trim().isEmpty()) {
                secret = "b7a3161ef64b7ea4afb344ba5a121e7f69083e4ff3aaae402d5dd65f500f6d2c";
            }

            secret = secret.trim().replaceAll("^[\"']|[\"']$", "");
        } catch (Exception e) {
            System.err.println("Erro ao acessar JWT_SECRET: " + e.getMessage());
            throw new RuntimeException("Erro ao acessar JWT_SECRET: " + e.getMessage(), e);
        }
    }

    public String generateToken(User user) {
        if (user == null || user.getId() == null)
            throw new IllegalArgumentException("Invalid user for token generation");

        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", user.getId());
            claims.put("name", user.getName());
            claims.put("email", user.getEmail());
            claims.put("role", user.getRole() != null ? user.getRole().toString() : "Guest");

            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getId().toString())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT token", e);
        }
    }

    public Claims validateToken(String token) throws JwtException {
        if (token == null || token.isEmpty()) {
            throw new JwtException("Token empty or null");
        }

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw e;
        } catch (Exception e) {
            throw new JwtException("Error validating token: " + e.getMessage());
        }
    }

    public boolean isTokenValid(String token) {
        if (token == null || token.isEmpty())
            return false;
        try {
            validateToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = validateToken(token);
            return Long.parseLong(claims.getSubject());
        } catch (JwtException e) {
            return null;
        }
    }

    public Instant getExpirationTime() {
        return Instant.now().plus(24, ChronoUnit.HOURS);
    }

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}