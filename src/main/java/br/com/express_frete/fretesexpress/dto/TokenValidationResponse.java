package br.com.express_frete.fretesexpress.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO for token validation response
 */
@Data
public class TokenValidationResponse {

    private boolean valid;

    @JsonProperty("user_id")
    private Long userId;
    private String name;
    private String email;
    private String role;
}