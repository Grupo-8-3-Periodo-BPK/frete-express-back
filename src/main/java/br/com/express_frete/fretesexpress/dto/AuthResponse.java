package br.com.express_frete.fretesexpress.dto;

import br.com.express_frete.fretesexpress.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO for authentication response
 */
@Data
public class AuthResponse {

    private String token;
    private Long id;
    private String name;
    private String email;

    @JsonProperty("role")
    private Role role;
}