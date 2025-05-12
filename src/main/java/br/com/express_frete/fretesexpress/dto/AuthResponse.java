package br.com.express_frete.fretesexpress.dto;

import br.com.express_frete.fretesexpress.model.enums.Role;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO for authentication response
 */
@Data
public class AuthResponse {
    private Long id;
    private String name;
    private String email;
    private Instant expiration;

    @JsonProperty("role")
    private Role role;
}