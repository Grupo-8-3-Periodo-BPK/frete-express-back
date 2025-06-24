package br.com.express_frete.fretesexpress.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import br.com.express_frete.fretesexpress.Validation.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/recovery/**", "/api/directions/**", "/api/geocode/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/register/client", "/api/users/register/driver")
                        .permitAll()
                        .requestMatchers("/api/users/**").hasAnyAuthority("ROLE_CLIENT", "ROLE_DRIVER", "ROLE_ADMIN")

                        // Rotas de Admin
                        .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")

                        // Permissões para Contratos
                        .requestMatchers(HttpMethod.GET, "/api/contracts/**")
                        .hasAnyAuthority("ROLE_CLIENT", "ROLE_DRIVER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/contracts").hasAuthority("ROLE_DRIVER")
                        .requestMatchers(HttpMethod.PATCH, "/api/contracts/**")
                        .hasAnyAuthority("ROLE_CLIENT", "ROLE_DRIVER", "ROLE_ADMIN")

                        // Permissões para Fretes (NOVA REGRA)
                        .requestMatchers("/api/freights/**").hasAnyAuthority("ROLE_CLIENT", "ROLE_DRIVER", "ROLE_ADMIN")

                        // Permissões para Tracking
                        .requestMatchers("/api/tracking/**").hasAnyAuthority("ROLE_CLIENT", "ROLE_DRIVER", "ROLE_ADMIN")

                        // Swagger/OpenAPI endpoints
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("{\"message\": \"Acesso não autorizado. Erro: "
                                    + authException.getMessage() + "\"}");
                        }))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "http://localhost:5173",
                "http://localhost:8080",
                "http://localhost:8000"));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}